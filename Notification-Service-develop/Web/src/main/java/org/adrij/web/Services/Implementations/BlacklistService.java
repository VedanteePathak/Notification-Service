package org.adrij.web.Services.Implementations;

import lombok.extern.slf4j.Slf4j;

import org.adrij.common.Exceptions.CustomException;
import org.adrij.common.Exceptions.RequestNotFoundCustomException;
import org.adrij.common.Models.DTOs.Requests.BlacklistRequestDTO;
import org.adrij.common.Models.DTOs.Responses.ResponseDTO;
import org.adrij.common.Models.Entities.BlacklistEntity;
import org.adrij.common.Repositories.BlacklistRepository;
import org.adrij.common.Services.RedisServiceAdapter;

import org.adrij.web.Services.BlacklistServiceAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class BlacklistService implements BlacklistServiceAdapter {
    private final BlacklistRepository blacklistRepository;
    private final RedisServiceAdapter redisService;
    private final String LOGGING_PREFIX = "Blacklist Service | ";
    private static final int BATCH_SIZE = 3;

    @Autowired
    public BlacklistService(
            BlacklistRepository blacklistRepository,
            RedisServiceAdapter redisService) {
        this.blacklistRepository = blacklistRepository;
        this.redisService = redisService;
    }

    public ResponseEntity<ResponseDTO> addPhoneNumbersToBlacklist(BlacklistRequestDTO blacklistRequestDTO) {
        String ADD_BL_LOGGING_PREFIX = LOGGING_PREFIX + "Add Phone Number to Blacklist | ";

        try {
            ArrayList<String> phoneNumbers = blacklistRequestDTO.getPhoneNumbers();

            log.info("{}Parameter | Phone Numbers in Blacklist Requests DTO = {}",
                    ADD_BL_LOGGING_PREFIX, phoneNumbers);

            ArrayList<BlacklistEntity> uniqueBlacklistRequests = phoneNumbers.stream()
                    .filter(phoneNumber -> !blacklistRepository.existsByPhoneNumber(phoneNumber))
                    .map(BlacklistEntity::new)
                    .collect(Collectors.toCollection(ArrayList::new));
            blacklistRepository.saveAll(uniqueBlacklistRequests);
            
            log.info("{}Added Phone Numbers to blacklist table successfully.",
                    ADD_BL_LOGGING_PREFIX);
            
            redisService.addPhoneNumbersToBlacklist(phoneNumbers);
            log.info("{}Added Phone numbers to blacklist Redis cache successfully.",
                    ADD_BL_LOGGING_PREFIX);
            
            ResponseDTO<String,String> responseDTO = new ResponseDTO<>("Successfully blacklisted", null);

            return ResponseEntity.ok(responseDTO);

        } catch (CustomException e) {
            log.error("{}Exception Class = {} | Exception Code = {} | Exception Message = {}",
                    ADD_BL_LOGGING_PREFIX, e.getClass(), e.getErrorCode(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("{}Exception Class = {} | Exception Message = {}",
                    ADD_BL_LOGGING_PREFIX, e.getClass(), e.getMessage());
            throw new CustomException("UNKNOWN_ERROR",
                    "Failed to blacklist phone numbers. Unexpected error occurred while adding to DB or Redis cache. Exact: "
                            + e.getMessage());
        }
    }

    public ResponseEntity<ResponseDTO> addPhoneNumbersToBlacklistInBatches(BlacklistRequestDTO blacklistRequestDTO) {
        String ADD_BL_BATCHES_LOGGING_PREFIX = LOGGING_PREFIX + "Add Phone Number to Blacklist In Batches | ";

        try {
            ArrayList<String> phoneNumbers = blacklistRequestDTO.getPhoneNumbers();

            log.info("{}Parameter | Phone Numbers in Blacklist Requests DTO = {}",
                    ADD_BL_BATCHES_LOGGING_PREFIX, phoneNumbers);

            int totalCountOfPhoneNumbers = phoneNumbers.size();
            for (int i = 0; i < totalCountOfPhoneNumbers; i += BATCH_SIZE) {
                ArrayList<String> batchPhoneNumbers =
                        new ArrayList<>(phoneNumbers.subList(i, Math.min(i + BATCH_SIZE, totalCountOfPhoneNumbers)));
                processBatchAddToBlacklist(batchPhoneNumbers);
            }

            log.info("{}Added Phone Numbers to blacklist table in batches successfully.",
                    ADD_BL_BATCHES_LOGGING_PREFIX);
            ResponseDTO<String,String> responseDTO = new ResponseDTO<>("Successfully blacklisted in batches", null);

            return ResponseEntity.ok(responseDTO);

        } catch (CustomException e) {
            log.error("{}Exception Class = {} | Exception Code = {} | Exception Message = {}",
                    ADD_BL_BATCHES_LOGGING_PREFIX, e.getClass(), e.getErrorCode(), e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("{}Exception Class = {} | Exception Message = {}",
                    ADD_BL_BATCHES_LOGGING_PREFIX, e.getClass(), e.getMessage());
            throw new CustomException("UNKNOWN_ERROR",
                    "Failed to blacklist phone numbers. Unexpected error occurred while adding to DB or Redis cache. Exact: "
                            + e.getMessage());
        }

        // todo: redis and db in different threads. Executor service.
    }

    private void processBatchAddToBlacklist(ArrayList<String> phoneNumbers) {
        try {
            phoneNumbers.stream()
                    .distinct()
                    .filter(phoneNumber -> !blacklistRepository.existsByPhoneNumber(phoneNumber))
                    .forEach(phoneNumber -> blacklistRepository.save(new BlacklistEntity(phoneNumber)));
        } catch (Exception e) {
            log.error("{}Process Batch Add to Blacklist | Exception Class = {} | Exception Message = {}",
                    LOGGING_PREFIX, e.getClass(), e.getMessage());
            throw new CustomException("BATCH_ADDITION_ERROR",
                    "Error occurred during batch addition to blacklist. Exact: " + e.getMessage());
        }
    }


    public ResponseEntity<ResponseDTO> removePhoneNumbersFromBlacklist(BlacklistRequestDTO blacklistRequestDTO) {
        String REMOVE_BL_LOGGING_PREFIX = LOGGING_PREFIX + "Remove Phone Numbers from Blacklist | ";

        try {
            ArrayList<String> phoneNumbers = blacklistRequestDTO.getPhoneNumbers();

            log.info("{}Parameters | Phone Numbers in Blacklist Request DTO = {}",
                    REMOVE_BL_LOGGING_PREFIX, phoneNumbers);

            ArrayList<BlacklistEntity> existingPhoneNumbersToRemove =
                    blacklistRepository.findByPhoneNumberIn(phoneNumbers);

            blacklistRepository.deleteAll(existingPhoneNumbersToRemove);
            log.info("{}Removed phone numbers from blacklist table in DB.", REMOVE_BL_LOGGING_PREFIX);

            ResponseDTO<String, String> responseDTO =
                    new ResponseDTO<>("Successfully whitelisted", null);

            return ResponseEntity.ok(responseDTO);

        } catch (CustomException e) {
            log.error("{}Exception Class = {} | Exception Code = {} | Exception Message = {}",
                    REMOVE_BL_LOGGING_PREFIX, e.getClass(), e.getErrorCode(), e.getMessage());

            throw e;
        } catch (Exception e) {
            log.error("{}Exception Class = {} | Exception Message = {}",
                    REMOVE_BL_LOGGING_PREFIX, e.getClass(), e.getMessage());

            throw new CustomException("UNKNOWN_ERROR",
                    "Failed to whitelist phone numbers. Unexpected error occurred while deleting from DB and Redis cache. Exact: "
                            + e.getMessage());
        }
    }

    public ResponseEntity<ResponseDTO> removePhoneNumbersFromBlacklistInBatches(BlacklistRequestDTO blacklistRequestDTO) {
        String REMOVE_BL_BATCHES_LOGGING_PREFIX = LOGGING_PREFIX + "Remove Phone Numbers from Blacklist | ";

        try {
            ArrayList<String> phoneNumbers = blacklistRequestDTO.getPhoneNumbers();

            log.info("{}Parameters | Phone Numbers in Blacklist Request DTO = {}",
                    REMOVE_BL_BATCHES_LOGGING_PREFIX, phoneNumbers);

            int totalCountOfPhoneNumbers = phoneNumbers.size();
            for (int i = 0; i < totalCountOfPhoneNumbers; i += BATCH_SIZE) {
                ArrayList<String> batchPhoneNumbers =
                        new ArrayList<>(phoneNumbers.subList(i, Math.min(i + BATCH_SIZE, totalCountOfPhoneNumbers)));
                processBatchRemoveFromBlacklist(batchPhoneNumbers);
            }
            log.info("{}Removed phone numbers from blacklist table in db.", REMOVE_BL_BATCHES_LOGGING_PREFIX);

//            redisService.removePhoneNumbersFromBlacklistInBatches(phoneNumbers);
            log.info("{}Removed phone numbers from Redis cache.", REMOVE_BL_BATCHES_LOGGING_PREFIX);

            ResponseDTO<String, String> responseDTO =
                    new ResponseDTO<>("Successfully whitelisted", null);

            return ResponseEntity.ok(responseDTO);

        } catch (CustomException e) {
            log.error("{}Exception Class = {} | Exception Code = {} | Exception Message = {}",
                    REMOVE_BL_BATCHES_LOGGING_PREFIX, e.getClass(), e.getErrorCode(), e.getMessage());

            throw e;
        } catch (Exception e) {
            log.error("{}Exception Class = {} | Exception Message = {}",
                    REMOVE_BL_BATCHES_LOGGING_PREFIX, e.getClass(), e.getMessage());

            throw new CustomException("UNKNOWN_ERROR",
                    "Failed to whitelist phone numbers. Unexpected error occurred while deleting from DB and Redis cache. Exact: "
                            + e.getMessage());
        }
    }

    private void processBatchRemoveFromBlacklist(ArrayList<String> phoneNumbers) {
        try {
            phoneNumbers.stream()
                    .distinct()
                    .filter(blacklistRepository::existsByPhoneNumber)
                    .forEach(blacklistRepository::deleteByPhoneNumber);
        } catch (Exception e) {
            log.error("{}Process Batch Remove from Blacklist | Exception Class = {} | Exception Message = {}",
                    LOGGING_PREFIX, e.getClass(), e.getMessage());
            throw new CustomException("BATCH_REMOVAL_ERROR",
                    "Error occurred during batch removal from blacklist. Exact: " + e.getMessage());
        }
    }

    public ResponseEntity<ResponseDTO> getAllPhoneNumbersInBlacklist() {
        String GET_BL_LOGGING_PREFIX = LOGGING_PREFIX + "Get All Phone Numbers in Blacklist | ";

        try{
            ResponseDTO response;

            ArrayList<String> allBlacklistedPhoneNumbersInDB =
                    blacklistRepository.findAll()
                            .stream()
                            .map(BlacklistEntity::getPhoneNumber)
                            .collect(Collectors.toCollection(ArrayList::new));

            log.info("{}MySQL DB Blacklisted Phone Numbers = {}",
                    GET_BL_LOGGING_PREFIX, allBlacklistedPhoneNumbersInDB);

            if (!allBlacklistedPhoneNumbersInDB.isEmpty()) {
                log.info("{}All blacklisted phone numbers sent as response.",
                        GET_BL_LOGGING_PREFIX);

                response = new ResponseDTO<>(
                        allBlacklistedPhoneNumbersInDB,
                        null);
            } else {
                log.error("{}Blacklist is empty.",
                        GET_BL_LOGGING_PREFIX);

                throw new RequestNotFoundCustomException("No phone number is present in the blacklist.");
            }

            return ResponseEntity.ok(response);

        } catch (CustomException e) {
            log.error("{}Exception Class = {} | Exception Code = {} | Exception Message = {}",
                    GET_BL_LOGGING_PREFIX, e.getClass(), e.getErrorCode(), e.getMessage());

            throw e;
        }
    }
}
