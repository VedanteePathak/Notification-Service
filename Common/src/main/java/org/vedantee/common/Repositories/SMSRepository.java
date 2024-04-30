package org.vedantee.common.Repositories;

import org.vedantee.common.Models.Entities.SMSEntity;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@EnableAutoConfiguration
public interface SMSRepository extends JpaRepository<SMSEntity, Long> {
    Optional<SMSEntity> findByRequestId(Long requestId);

    boolean existsByRequestId(Long requestId);
}

