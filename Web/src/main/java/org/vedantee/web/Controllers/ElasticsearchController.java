package org.adrij.web.Controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Digits;

import static org.adrij.web.Constants.APIEndPoints.*;

import org.adrij.common.Services.ElasticsearchServiceAdapter;
import org.adrij.common.Models.DTOs.Requests.ElasticSMSDateRequestDTO;
import org.adrij.common.Models.Entities.ElasticSMSDocument;

@Controller
@RestController
@Validated
@Slf4j
public class ElasticsearchController {
    private final ElasticsearchServiceAdapter elasticsearchService;
    private final String LOGGING_PREFIX = "Elasticsearch Controller | ";

    @Autowired
    public ElasticsearchController(ElasticsearchServiceAdapter elasticsearchService) {
        this.elasticsearchService = elasticsearchService;
    }

    @GetMapping(SEARCH_BY_TEXT_ENDPOINT)
    public ResponseEntity<Page<ElasticSMSDocument>> searchElasticSMSRequestContainingText(
            @Valid
            @PathVariable("text") String text,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("{}Get SMS Containing Text | Parameters: Text = {}, Number of Pages = {}, Size of each page = {}",
                LOGGING_PREFIX, text, page, size);
        return ResponseEntity.ok(elasticsearchService.getSMSContainingText(text, page, size));
    }

    @GetMapping(SEARCH_BY_REQUEST_ID_ENDPOINT)
    @Validated
    public ResponseEntity<Page<ElasticSMSDocument>> searchElasticSMSRequestByRequestId(
            @Valid
            @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "Request ID must be a valid Integer or Long.")
            @PathVariable String requestId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("{}Get SMS Containing Text | Parameter: RequestId = {}, PageNo = {}, PageSize = {}",
                LOGGING_PREFIX, requestId, page, size);
        return ResponseEntity.ok(elasticsearchService.getSMSByRequestId(requestId, page, size));
    }

    @GetMapping(SEARCH_BY_PHONENUMBER_IN_DATERANGE_ENDPOINT)
    @Validated
    public ResponseEntity<Page<ElasticSMSDocument>> searchElasticSMSRequestByPhoneNumberInDateRange(
            @Valid
            @RequestBody ElasticSMSDateRequestDTO elasticSMSDateRequestDTO,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("{}Search by Phone Number in Date Range | Parameters | ElasticSMSDateRequestDTO: Phone Number = {}, Begin = {}, End = {} | PageNo = {}, PageSize = {}",
                LOGGING_PREFIX, elasticSMSDateRequestDTO.getPhoneNumber(), elasticSMSDateRequestDTO.getBegin(), elasticSMSDateRequestDTO.getEnd(), page, size);
        return ResponseEntity.ok(
                elasticsearchService.getSMSByPhoneNumberInDateRange(elasticSMSDateRequestDTO, page, size));
    }
}
