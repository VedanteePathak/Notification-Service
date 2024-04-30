package org.adrij.web.Services.Implementations;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

import org.adrij.common.Exceptions.CustomException;
import org.adrij.common.Exceptions.RequestNotFoundCustomException;
import org.adrij.common.Models.DTOs.Requests.SMSRequestDTO;
import org.adrij.common.Models.DTOs.Responses.ResponseDTO;
import org.adrij.common.Models.DTOs.Responses.SuccessDetails;
import org.adrij.common.Models.Entities.SMSEntity;
import org.adrij.common.Repositories.SMSRepository;
import org.adrij.common.Services.RedisServiceAdapter;

import org.adrij.web.Services.SMSServiceAdapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import redis.clients.jedis.Jedis;

@Service
@EnableScheduling
@Slf4j
public class SMSService implements SMSServiceAdapter {
    private final SMSRepository smsRepository;
    private final KafkaProducerService kafkaProducerService;
    private final RedisServiceAdapter redisService;
    private final String LOGGING_PREFIX = "SMS Service | ";
    private static final int MAX_REQUESTS_PER_MINUTE = 2;

    //TODO: put in env

    @Autowired
    public SMSService(SMSRepository smsRepository,
                      KafkaProducerService kafkaProducerService,
                      RedisServiceAdapter redisService) {
        this.smsRepository = smsRepository;
        this.kafkaProducerService = kafkaProducerService;
        this.redisService = redisService;
    }

    @Override
    public ResponseEntity<ResponseDTO> sendSMS(SMSRequestDTO smsRequestDTO, String clientId) {
        String SEND_SMS_LOGGING_PREFIX = LOGGING_PREFIX + "Send SMS | ";
        try {
            log.info("{}Client Id = {} | SMS Requests DTO Received: Phone Number = {}, Message = {}",
                    SEND_SMS_LOGGING_PREFIX, clientId, smsRequestDTO.getPhoneNumber(), smsRequestDTO.getMessage());

            long timestamp = System.currentTimeMillis() / 1000;
            String key = clientId + ":" + timestamp;

            try (Jedis jedis = new Jedis("localhost")) {
                long count = jedis.incr(key);
                jedis.expire(key, 5); // Set key expiration to 60 seconds

                if (count > MAX_REQUESTS_PER_MINUTE) {
                    log.error("{}Rate limit exceeded for Client Id: {}", SEND_SMS_LOGGING_PREFIX, clientId);
                    throw new CustomException(
                            "RATE_LIMIT_EXCEEDED",
                            "Rate limit exceeded for the client id = " + clientId
                    );
                }
            }

            if (redisService.contains(smsRequestDTO.getPhoneNumber())) {
                log.error("{}Blacklisted Phone Number requested (Already present in redis cache).",
                        SEND_SMS_LOGGING_PREFIX);
                throw new CustomException("REQUEST_NOT_SAVED", "Blacklisted Phone Number.");
            } else {
                SMSEntity smsRequest = SMSEntity.builder()
                        .phoneNumber(smsRequestDTO.getPhoneNumber())
                        .message(smsRequestDTO.getMessage())
                        .status("REQUEST_SAVED")
                        .build();

                smsRepository.save(smsRequest);

                log.info("{}Requests Saved to DB with Requests Id {}",
                        SEND_SMS_LOGGING_PREFIX, smsRequest.getRequestId());

                kafkaProducerService.produceKafkaMessage(smsRequest.getRequestId());

                // TODO: client id distribution. Unique to a user.

                log.info("{}Requests Id Produced by Kafka Producer.",
                        SEND_SMS_LOGGING_PREFIX);

                ResponseDTO successResponse =
                        new ResponseDTO<>(
                                new SuccessDetails(
                                        smsRequest.getRequestId(),
                                        "Requests Successfully Sent"),
                                null
                        );

                log.info("SMS Service | Send SMS | Successfully Sent SMS Requests");
                return ResponseEntity.ok(successResponse);
            }
        } catch (CustomException e) {
            log.error("{}Error: {}", SEND_SMS_LOGGING_PREFIX, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("{}Unknown Error: {}", SEND_SMS_LOGGING_PREFIX, e.getMessage());
            throw new CustomException("UNKNOWN_ERROR",
                    "Unexpected error occurred while sending message. Exact: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<ResponseDTO> getSMSbyRequestId(String requestId, String clientId) {
        String GET_SMS_LOGGING_PREFIX = LOGGING_PREFIX + "| Get SMS by Request Id | ";

        try {
            log.info("{}Client Id Received = {} | Request Id Received: {}",
                    GET_SMS_LOGGING_PREFIX, clientId, requestId);

            long timestamp = System.currentTimeMillis() / 1000;
            String key = clientId + ":" + timestamp;

            try (Jedis jedis = new Jedis("localhost")) {
                long count = jedis.incr(key);
                jedis.expire(key, 5); // Set key expiration to 60 seconds

                if (count > MAX_REQUESTS_PER_MINUTE) {
                    log.error("{}Rate limit exceeded for Client Id: {}",
                            GET_SMS_LOGGING_PREFIX, clientId);

                    throw new CustomException(
                            "RATE_LIMIT_EXCEEDED",
                            "Rate limit exceeded for the client id = " + clientId
                    );
                }
            }

            long requestIdLong = Long.parseLong(requestId);
            Optional<SMSEntity> smsRequest = smsRepository.findByRequestId(requestIdLong);

            if (smsRequest.isEmpty()) {
                log.error("{}Requests Id not found in DB.", GET_SMS_LOGGING_PREFIX);
                throw new RequestNotFoundCustomException("Requests Id NOT found in DB.");
            } else {
                ResponseDTO successResponse =
                        new ResponseDTO<>(
                                smsRequest,
                                null
                        );

                log.info("{}Entry fetched successfully from DB Table sms_requests: {}",
                        GET_SMS_LOGGING_PREFIX, smsRequest);

                return ResponseEntity.ok(successResponse);
            }
        } catch (NumberFormatException e) {
            log.error("{}String {} cannot be converted to Integer or Long | Exception Class = {} | Exception Message = {}",
                    GET_SMS_LOGGING_PREFIX, requestId, e.getClass(), e.getMessage());

            throw new CustomException("NUMBER_CONVERSION_ERROR", "Requests Id is NOT a valid Integer or Long. " + e.getMessage());
        } catch (CustomException e) {
            log.error("{}Exception Class = {} | Exception Message = {}",
                    GET_SMS_LOGGING_PREFIX, e.getClass(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("{}Unknown Error: {}",
                    GET_SMS_LOGGING_PREFIX, e.getMessage());

            throw new CustomException("UNKNOWN_ERROR",
                    "Unexpected error occurred while fetching. Exact: " + e.getMessage());
        }
    }
}

