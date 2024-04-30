package org.adrij.common.Services.Implementations;

import org.adrij.common.Services.ElasticsearchServiceAdapter;
import org.adrij.common.Models.DTOs.Requests.ElasticSMSDateRequestDTO;
import org.adrij.common.Exceptions.ElasticsearchCustomException;
import org.adrij.common.Models.Entities.ElasticSMSDocument;
import org.adrij.common.Repositories.ElasticSMSRepository;

import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;



@Service
@Slf4j
public class ElasticsearchService implements ElasticsearchServiceAdapter {
    private final ElasticSMSRepository elasticSMSRepository;
    private final String LOGGING_PREFIX = "Elasticsearch Service | ";
    private final String ELASTICSEARCH_ERROR_MESSAGE = "Error occurred while processing data in elastic search.";

    @Autowired
    public ElasticsearchService(ElasticSMSRepository elasticSMSRepository) {
        try {
            this.elasticSMSRepository = elasticSMSRepository;
        } catch (Exception e) {
            log.error("{}Constructor | Exception Class = {} | Exception Message = {}",
                    LOGGING_PREFIX, e.getClass(), e.getMessage());
            throw new ElasticsearchCustomException(
                    String.format("%s Exact: %s", ELASTICSEARCH_ERROR_MESSAGE, e.getMessage())
            );
        }
    }

    public void saveToElasticsearch(ElasticSMSDocument elasticSMSDocument) {
        try {
            log.info("{}Save to Elasticsearch | Elastic SMS Request : {}",
                    LOGGING_PREFIX, elasticSMSDocument);
            elasticSMSRepository.save(elasticSMSDocument);
        } catch (DataAccessResourceFailureException e) {
            log.error("{}Save to Elasticsearch | Ignored Runtime Exception: Unable to parse Responses body for Responses. {}",
                    LOGGING_PREFIX, e.getMessage());

        } catch (Exception e) {
            log.error("{}Save to Elasticsearch | Exception Class = {} | Exception Message = {}",
                    LOGGING_PREFIX, e.getClass(), e.getMessage());
            throw new ElasticsearchCustomException(
                    String.format("%s Exact: %s", ELASTICSEARCH_ERROR_MESSAGE, e.getMessage())
            );
        }
    }

    public Page<ElasticSMSDocument> getSMSContainingText(String text, int pageNo, int pageSize) {
        try {
            log.info("{}Get SMS That Contains | Parameters | Text = {}, PageNo = {}, PageSize = {}",
                    LOGGING_PREFIX, text, pageNo, pageSize);

            Pageable pageable = PageRequest.of(pageNo, pageSize);
            Page<ElasticSMSDocument> result
                    = elasticSMSRepository.findByMessageContaining(text, pageable);

            log.info("{}Get SMS That Contains | Query Responses = {}",
                    LOGGING_PREFIX, result);

            return result;
        } catch (Exception e) {
            log.error("{}Get SMS That Contains | Exception Class = {} | Exception Message = {}",
                    LOGGING_PREFIX, e.getClass(), e.getMessage());
            throw new ElasticsearchCustomException(
                    String.format("%s Exact: %s", ELASTICSEARCH_ERROR_MESSAGE, e.getMessage())
            );
        }
    }

    public Page<ElasticSMSDocument> getSMSByRequestId(String requestId, int pageNo, int pageSize) {
        try {
            log.info("{}Get SMS By Requests Id | Parameters: RequestId = {}, PageNo = {}, PageSize = {}",
                    LOGGING_PREFIX, requestId, pageNo, pageSize);

            Pageable pageable = PageRequest.of(pageNo, pageSize);
            long requestIdLong = Long.parseLong(requestId);
            Page<ElasticSMSDocument> result =
                    elasticSMSRepository.findElasticSMSRequestByRequestId(
                            requestIdLong, pageable);

            log.info("{}Get SMS By Requests Id | Query Responses: {}", LOGGING_PREFIX, result);

            return result;
        } catch (Exception e) {
            log.error("{}Get SMS by Request Id | Exception Class = {} | Exception Message = {}",
                    LOGGING_PREFIX, e.getClass(), e.getMessage());
            throw new ElasticsearchCustomException(
                    String.format("%s Exact: %s", ELASTICSEARCH_ERROR_MESSAGE, e.getMessage())
            );
        }
    }

    public Page<ElasticSMSDocument> getSMSByPhoneNumberInDateRange(
            ElasticSMSDateRequestDTO elasticSMSDateRequestDTO, int pageNo, int pageSize) {
        try {
            log.info("{}Get SMS By Phone Number in Date Range | Parameters | ElasticSMSDateRequestDTO: Phone Number = {}, Begin = {}, End = {} | PageNo = {}, PageSize = {}",
                    LOGGING_PREFIX,
                    elasticSMSDateRequestDTO.getPhoneNumber(),
                    elasticSMSDateRequestDTO.getBegin(),
                    elasticSMSDateRequestDTO.getEnd(),
                    pageNo, pageSize);

            Pageable pageable = PageRequest.of(pageNo, pageSize);
            String phoneNumber = elasticSMSDateRequestDTO.getPhoneNumber();
            LocalDateTime begin = elasticSMSDateRequestDTO.getBegin();
            LocalDateTime end = elasticSMSDateRequestDTO.getEnd();
            Page<ElasticSMSDocument> result =
                    elasticSMSRepository.findByPhoneNumberAndUpdatedAtBetween(
                    phoneNumber, begin, end, pageable);

            log.info("{}Get SMS By Phone Number in Date Range | Query Responses: {}", LOGGING_PREFIX, result);

            return result;
        } catch (Exception e) {
            log.error("{}Get SMS by Request Id | Exception Class = {} | Exception Message = {}",
                    LOGGING_PREFIX, e.getClass(), e.getMessage());
            throw new ElasticsearchCustomException(
                    String.format("%s Exact: %s", ELASTICSEARCH_ERROR_MESSAGE, e.getMessage())
            );
        }
    }
}