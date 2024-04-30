package org.vedantee.common.Services.Implementations;

import lombok.extern.slf4j.Slf4j;
import org.vedantee.common.Exceptions.RedisCustomException;
import org.vedantee.common.Services.RedisServiceAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.vedantee.common.Constants.RedisConstants.*;

@Service
@Slf4j
public class RedisService implements RedisServiceAdapter {
    private final Jedis jedis;
    private final ExecutorService executorService;// Adjust thread pool size as needed
    private final String LOGGING_PREFIX = "Redis Service | ";
    private final String REDIS_ERROR_MESSAGE = "Error occurred in Redis.";

    @Autowired
    public RedisService(Jedis jedis) {
        try {
            this.jedis = jedis;
            this.executorService = Executors.newFixedThreadPool(10);
        } catch (Exception e) {
            log.error("{}Constructor | Exception Class = {} | Exception Message = {}",
                    LOGGING_PREFIX, e.getClass(), e.getMessage());
            throw new RedisCustomException(
                    String.format("%s Exact: %s", REDIS_ERROR_MESSAGE, e.getMessage())
            );
        }
    }

    public boolean contains(String phoneNumber) {
        String CONTAINS_LOGGING_PREFIX = LOGGING_PREFIX + "Contains | ";

        try {
            log.info("{}Parameters | Phone Number = {}",
                    CONTAINS_LOGGING_PREFIX, phoneNumber);
            boolean result = jedis.sismember(BLACKLIST_REDIS_KEY, phoneNumber);
            log.info("{}Search result = {}",
                    CONTAINS_LOGGING_PREFIX, result);
            return result;
        } catch (Exception e) {
            log.error("{}Exception Class = {} | Exception Message = {}",
                    CONTAINS_LOGGING_PREFIX, e.getClass(), e.getMessage());
            throw new RedisCustomException(
                    String.format("%s Exact: %s", REDIS_ERROR_MESSAGE, e.getMessage())
            );
        }
    }

    public void addPhoneNumbersToBlacklist(ArrayList<String> phoneNumbers) {
        String ADD_BL_LOGGING_PREFIX = LOGGING_PREFIX + "Add Phone Numbers To Blacklist | ";

        try {
            log.info("{}Parameters | Phone Numbers = {}",
                    ADD_BL_LOGGING_PREFIX, phoneNumbers);
            for (String number : phoneNumbers) {
                jedis.sadd(BLACKLIST_REDIS_KEY, number);
            }
            jedis.expire(BLACKLIST_REDIS_KEY, TIME_TO_LIVE_IN_SECONDS);
            log.info("{}Successfully added phone numbers to blacklist.",
                    ADD_BL_LOGGING_PREFIX);
        } catch (Exception e) {
            log.error("{}Exception Class = {} | Exception Message = {}",
                    ADD_BL_LOGGING_PREFIX, e.getClass(), e.getMessage());
            throw new RedisCustomException(
                    String.format("%s Exact: %s", REDIS_ERROR_MESSAGE, e.getMessage())
            );
        }
    }

    public void addPhoneNumbersToBlacklistInBatches(ArrayList<String> phoneNumbers) {
        String ADD_BL_BATCHES_LOGGING_PREFIX = LOGGING_PREFIX + "Add Phone Numbers To Blacklist In Batches | ";

        try {
            log.info("{}Parameters | Phone Numbers = {}",
                    ADD_BL_BATCHES_LOGGING_PREFIX, phoneNumbers);
            int totalPhoneNumbers = phoneNumbers.size();
            for (int i = 0; i < totalPhoneNumbers; i += BATCH_SIZE) {
                ArrayList<String> batchPhoneNumbers
                        = new ArrayList<>(phoneNumbers.subList(i, Math.min(i + BATCH_SIZE, totalPhoneNumbers)));
                executorService.submit(() -> processBatchAddToBlacklist(batchPhoneNumbers));
            }
            executorService.shutdown();
            while (!executorService.isTerminated()) {
                // Wait for all tasks to complete
            }
            jedis.expire(BLACKLIST_REDIS_KEY, TIME_TO_LIVE_IN_SECONDS);
            log.info("{}Successfully added phone numbers to blacklist.",
                    ADD_BL_BATCHES_LOGGING_PREFIX);
        } catch (Exception e) {
            log.error("{}Exception Class = {} | Exception Message = {}",
                    ADD_BL_BATCHES_LOGGING_PREFIX, e.getClass(), e.getMessage());
            throw new RedisCustomException(
                    String.format("%s Exact: %s", REDIS_ERROR_MESSAGE, e.getMessage())
            );
        }
    }

    private void processBatchAddToBlacklist(ArrayList<String> phoneNumbers) {
        phoneNumbers.parallelStream().distinct().forEach(phoneNumber -> {
            try {
                jedis.sadd(BLACKLIST_REDIS_KEY, phoneNumber);
            } catch (Exception e) {
                log.error("{}Process Batch Add to Blacklist | Exception Class = {} | Exception Message = {}",
                        LOGGING_PREFIX, e.getClass(), e.getMessage());
                throw new RedisCustomException(
                        String.format("%s Exact: %s", REDIS_ERROR_MESSAGE, e.getMessage())
                );
            }
        });
    }

    @Override
    public void removePhoneNumbersFromBlacklist(ArrayList<String> phoneNumbers) {
        String REMOVE_BL_LOGGING_PREFIX = LOGGING_PREFIX + "Remove Phone Numbers From Blacklist | ";

        try {
            log.info("{}Parameters | Phone Numbers = {}",
                    REMOVE_BL_LOGGING_PREFIX, phoneNumbers);
            for (String number : phoneNumbers) {
                jedis.srem(BLACKLIST_REDIS_KEY, number);
            }
            log.info("{}Successfully removed phone numbers from blacklist.",
                    REMOVE_BL_LOGGING_PREFIX);
        } catch (Exception e) {
            log.error("{}Exception Class = {} | Exception Message = {}",
                    REMOVE_BL_LOGGING_PREFIX, e.getClass(), e.getMessage());
            throw new RedisCustomException(
                    String.format("%s Exact: %s", REDIS_ERROR_MESSAGE, e.getMessage())
            );
        }
    }

    @Override
    public void removePhoneNumbersFromBlacklistInBatches(ArrayList<String> phoneNumbers) {
        String REMOVE_BL_BATCHES_LOGGING_PREFIX = LOGGING_PREFIX + "Remove Phone Numbers From Blacklist In Batches | ";

        try {
            log.info("{}Parameters | Phone Numbers = {}",
                    REMOVE_BL_BATCHES_LOGGING_PREFIX, phoneNumbers);
            int totalPhoneNumbers = phoneNumbers.size();
            for (int i = 0; i < totalPhoneNumbers; i += BATCH_SIZE) {
                ArrayList<String> batchPhoneNumbers = new ArrayList<>(phoneNumbers.subList(i, Math.min(i + BATCH_SIZE, totalPhoneNumbers)));
                executorService.submit(() -> processBatchRemoveFromBlacklist(batchPhoneNumbers));
            }
            executorService.shutdown();
            while (!executorService.isTerminated()) {
                // Wait for all tasks to complete
            }
            log.info("{}Successfully removed phone numbers from blacklist.",
                    REMOVE_BL_BATCHES_LOGGING_PREFIX);
        } catch (Exception e) {
            log.error("{}Exception Class = {} | Exception Message = {}",
                    REMOVE_BL_BATCHES_LOGGING_PREFIX, e.getClass(), e.getMessage());
            throw new RedisCustomException(
                    String.format("%s Exact: %s", REDIS_ERROR_MESSAGE, e.getMessage())
            );
        }
    }

    private void processBatchRemoveFromBlacklist(ArrayList<String> phoneNumbers) {
        phoneNumbers.parallelStream().forEach(phoneNumber -> {
            try {
                jedis.srem(BLACKLIST_REDIS_KEY, phoneNumber);
            } catch (Exception e) {
                log.error("{}Process Batch Remove from Blacklist | Exception Class = {} | Exception Message = {}",
                        LOGGING_PREFIX, e.getClass(), e.getMessage());
                throw new RedisCustomException(
                        String.format("%s Exact: %s", REDIS_ERROR_MESSAGE, e.getMessage())
                );
            }
        });
    }

    @Override
    public Set<String> getAllPhoneNumbersInBlacklist() {
        try {
            log.info("{}Get All Phone Numbers in Blacklist.",
                    LOGGING_PREFIX);
            Set<String> phoneNumbersInBlacklist = jedis.smembers(BLACKLIST_REDIS_KEY);
            log.info("{}Get All Phone Numbers in Blacklist | Successfully fetched all phone numbers from Redis blacklist.",
                    LOGGING_PREFIX);
            return phoneNumbersInBlacklist;
        } catch (Exception e) {
            log.error("{} Get All Phone Numbers in Blacklist | Exception Class = {} | Exception Message = {}",
                    LOGGING_PREFIX, e.getClass(), e.getMessage());
            throw new RedisCustomException(
                    String.format("%s Exact: %s", REDIS_ERROR_MESSAGE, e.getMessage())
            );
        }
    }
}
