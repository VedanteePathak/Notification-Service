package org.vedantee.common.Repositories;

import org.vedantee.common.Models.Entities.ElasticSMSDocument;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

@Repository
@EnableAutoConfiguration
public interface ElasticSMSRepository extends ElasticsearchRepository<ElasticSMSDocument, Long> {
    Page<ElasticSMSDocument> findByMessageContaining(String text, Pageable pageable);

    Page<ElasticSMSDocument> findElasticSMSRequestByRequestId(long id, Pageable pageable);

    Page<ElasticSMSDocument> findByPhoneNumberAndUpdatedAtBetween(
            String phoneNumber,
            LocalDateTime begin,
            LocalDateTime end,
            Pageable pageable);
}