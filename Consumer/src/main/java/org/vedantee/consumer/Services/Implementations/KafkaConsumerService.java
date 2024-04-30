package org.vedantee.consumer.Services.Implementations;

import org.vedantee.common.Repositories.BlacklistRepository;
import org.vedantee.common.Repositories.SMSRepository;
import org.vedantee.common.Models.Entities.ElasticSMSDocument;
import org.vedantee.common.Models.Entities.SMSEntity;
import org.vedantee.common.Services.RedisServiceAdapter;
import org.vedantee.common.Services.ElasticsearchServiceAdapter;
import org.vedantee.common.Exceptions.CustomException;
import org.vedantee.common.Exceptions.RedisCustomException;
import org.vedantee.common.Exceptions.ElasticsearchCustomException;
import org.vedantee.common.Exceptions.ThirdPartyAPICustomException;
import org.vedantee.common.Exceptions.DatabaseCustomException;

import org.vedantee.consumer.Services.KafkaConsumerServiceAdapter;
import org.vedantee.consumer.Services.ThirdPartyAPIServiceAdapter;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.vedantee.consumer.Constants.KafkaConsumerConstants;

@Service
@Slf4j
public class KafkaConsumerService implements KafkaConsumerServiceAdapter {
    private final SMSRepository smsRepository;
    private final RedisServiceAdapter redisService;
    private final BlacklistRepository blacklistRepository;
    private final ElasticsearchServiceAdapter elasticsearchService;
    private final ThirdPartyAPIServiceAdapter thirdPartyAPIService;
    private final String LOGGING_PREFIX = "Kafka Consumer Service | ";

    @Autowired
    public KafkaConsumerService(
            SMSRepository smsRepository,
            RedisServiceAdapter redisService,
            BlacklistRepository blacklistRepository,
            ThirdPartyAPIServiceAdapter thirdPartyAPIService,
            ElasticsearchServiceAdapter elasticsearchService) {
        this.smsRepository = smsRepository;
        this.redisService = redisService;
        this.blacklistRepository = blacklistRepository;
        this.thirdPartyAPIService = thirdPartyAPIService;
        this.elasticsearchService = elasticsearchService;
    }

    @KafkaListener(topics = KafkaConsumerConstants.CONSUMER_KAFKA_TOPIC_NAME, groupId = KafkaConsumerConstants.CONSUMER_GROUP_ID)
    public void consumeKafkaMessageAndSendSMS(String kafkaReceivedMessage) {

        String CONSUME_MESSAGE_LOGGING_PREFIX = LOGGING_PREFIX + "Consume Message | ";

        log.info("{}Message Received: {}",
                CONSUME_MESSAGE_LOGGING_PREFIX, kafkaReceivedMessage);

        long requestId = Long.parseLong(kafkaReceivedMessage);

        log.info("{}Message converted into Requests Id. Value = {}",
                CONSUME_MESSAGE_LOGGING_PREFIX, requestId);

        try {
            Optional<SMSEntity> smsEntityOptional = smsRepository.findByRequestId(requestId);

            if (smsEntityOptional.isPresent()) {
                SMSEntity smsEntity = smsEntityOptional.get();

                log.info("{}Requests Id is present in the database. SMS Requests Object fetched from database: {}",
                        CONSUME_MESSAGE_LOGGING_PREFIX, smsEntity);

                try {
                    String phoneNumber = smsEntity.getPhoneNumber();
                    log.info("{}Phone number in SMS Requests Object: {}",
                            CONSUME_MESSAGE_LOGGING_PREFIX, phoneNumber);

                    if (!isPhoneNumberBlacklisted(phoneNumber)) {
                        log.info("{}Phone number is NOT Blacklisted.", CONSUME_MESSAGE_LOGGING_PREFIX);

                        thirdPartyAPIService.sendSMSRestTemplate(
                                smsEntity.getPhoneNumber(),
                                smsEntity.getRequestId(),
                                smsEntity.getMessage()
                        );

                        smsEntity.setStatus("MESSAGE_SENT");

                        log.info("{}Message successfully sent through 3rd Party API. SMS_Text={}",
                                CONSUME_MESSAGE_LOGGING_PREFIX, smsEntity.getMessage());
                    } else {
                        log.error("{}Phone number is Blacklisted.", CONSUME_MESSAGE_LOGGING_PREFIX);

                        smsEntity.setStatus("FAILED");
                        smsEntity.setFailureCode("BLACK");
                        smsEntity.setFailureComments("Phone number is blacklisted.");

                        throw new CustomException("BLACKLISTED_PHONE_NUMBER", "Phone number is blacklisted.");
                    }

                } catch (RedisCustomException e) {
                    log.error("{}Error happened while sending SMS. ErrorCode={}, Message={}",
                            CONSUME_MESSAGE_LOGGING_PREFIX, e.getErrorCode(), e.getMessage());

                    smsEntity.setStatus("FAILED");
                    smsEntity.setFailureCode("CACHE_CRASH");
                    smsEntity.setFailureComments("Redis cache error. " + e.getMessage());

                    throw e;
                } catch (ThirdPartyAPICustomException e) {
                    log.error("{}Message NOT sent due to Third Party API Exception. ErrorCode={}, Message={}",
                            CONSUME_MESSAGE_LOGGING_PREFIX, e.getErrorCode(), e.getMessage());

                    smsEntity.setStatus("FAILED");
                    smsEntity.setFailureCode("API_FAILURE");
                    smsEntity.setFailureComments("External API could not send SMS. " + e.getMessage());

                    throw e;
                }

                log.info("{}Final state of SMS Requests Object: {}",
                        CONSUME_MESSAGE_LOGGING_PREFIX, smsEntity);

                try {
                    smsRepository.save(smsEntity);

                    log.info("{}SMS Requests object saved to sms_requests table in DB.",
                            CONSUME_MESSAGE_LOGGING_PREFIX);

                    ElasticSMSDocument elasticSMSDocument = new ElasticSMSDocument(smsEntity);

                    log.info("{}Elastic SMS Requests Document Object created. {}",
                            CONSUME_MESSAGE_LOGGING_PREFIX, elasticSMSDocument);

                    elasticsearchService.saveToElasticsearch(elasticSMSDocument);

                    log.info("{}Elastic SMS Requests Document Object successfully saved.",
                            CONSUME_MESSAGE_LOGGING_PREFIX);

                } catch (DatabaseCustomException e) {
                    log.error("{}SMS sent successfully. SMS Requests Object not saved in sms_requests table and Elasticsearch due to Database error in MySQL DB | Exception Class = {} | Exception Message = {}",
                            CONSUME_MESSAGE_LOGGING_PREFIX, e.getClass(), e.getMessage());

                    throw e;
                } catch (ElasticsearchCustomException e) {
                    log.error("{}SMS sent successfully. SMS Requests Object not saved Elasticsearch due to Elasticsearch Exception | Exception Class = {} | Exception Message = {}",
                            CONSUME_MESSAGE_LOGGING_PREFIX, e.getClass(), e.getMessage());

                    throw e;
                } catch (Exception e) {
                    log.error("{}SMS sent successfully. SMS Requests Object not saved in sms_requests table and Elasticsearch due to an unknown error in Elasticsearch or MySQL DB | Exception Class = {} | Exception Message = {}",
                            CONSUME_MESSAGE_LOGGING_PREFIX, e.getClass(), e.getMessage());

                    throw new CustomException("UNKNOWN_CONSUMER_ERROR", "SMS sent successfully. Unknown error in Elasticsearch or MySQL DB. Exact: " + e.getMessage());
                }
            } else {
                log.error("{}SMS NOT Sent. Requests Id received by Kafka Listener is absent from SMS Requests Database table.",
                        CONSUME_MESSAGE_LOGGING_PREFIX);

                throw new CustomException("REQUEST_ID_NOTINDB", "SMS NOT Sent. Requests Id received by Kafka Listener is absent in SMS Requests Database table.");
            }
        } catch (CustomException e) {
            log.error("{}Exception Class = {} | Exception Message = {}",
                    CONSUME_MESSAGE_LOGGING_PREFIX, e.getClass(), e.getMessage());

            throw e;
        } catch (Exception e) {
            log.error("{}SMS NOT Sent | Exception Class = {} | Exception Message = {}",
                    CONSUME_MESSAGE_LOGGING_PREFIX, e.getClass(), e.getMessage());

            throw new CustomException("UNKNOWN_CONSUMER_ERROR", "Error occurred in Kafka Consumer Service. Exact: "
                    + e.getMessage());
        }
    }

    public boolean isPhoneNumberBlacklisted(String phoneNumber) {
        String IS_BLACKLISTED_LOGGING_PREFIX = LOGGING_PREFIX + "Is Phone Number Blacklisted | ";

        try {
            log.info("{}Parameters | Phone Number = {}", IS_BLACKLISTED_LOGGING_PREFIX, phoneNumber);

            if (redisService.contains(phoneNumber)) {
                log.info("{}Phone number found blacklisted in Redis cache.", IS_BLACKLISTED_LOGGING_PREFIX);
                return true;
            }
            if (blacklistRepository.existsByPhoneNumber(phoneNumber)) {
                log.info("{}Phone number NOT blacklisted in Redis cache BUT blacklisted in blacklist DB table.",
                        IS_BLACKLISTED_LOGGING_PREFIX);
                return true;
            }
            log.info("{}Phone number NOT blacklisted.", IS_BLACKLISTED_LOGGING_PREFIX);
            return false;
        } catch(CustomException e) {
            log.error("{}Exception Class = {} | Exception Code = {} | Exception Message = {}",
                    IS_BLACKLISTED_LOGGING_PREFIX, e.getClass(), e.getErrorCode(), e.getMessage());
            throw e;
        } catch(Exception e) {
            log.error("{}SMS NOT Sent | Exception Class = {} | Exception Message = {}",
                    IS_BLACKLISTED_LOGGING_PREFIX, e.getClass(), e.getMessage());
            throw e;
        }
    }
}
