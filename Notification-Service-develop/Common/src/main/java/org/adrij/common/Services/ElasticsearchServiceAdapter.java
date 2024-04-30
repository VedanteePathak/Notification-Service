package org.adrij.common.Services;

import org.adrij.common.Models.DTOs.Requests.ElasticSMSDateRequestDTO;
import org.adrij.common.Models.Entities.ElasticSMSDocument;
import org.springframework.data.domain.Page;

public interface ElasticsearchServiceAdapter {
    void saveToElasticsearch(ElasticSMSDocument elasticSMSDocument);
    Page<ElasticSMSDocument> getSMSContainingText(String text, int page, int size);
    Page<ElasticSMSDocument> getSMSByRequestId(String requestId, int pageNo, int pageSize);
    Page<ElasticSMSDocument> getSMSByPhoneNumberInDateRange(
            ElasticSMSDateRequestDTO elasticSMSDateRequestDTO,
            int pageNo, int pageSize);
}
