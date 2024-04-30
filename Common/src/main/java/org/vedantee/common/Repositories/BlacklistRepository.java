package org.vedantee.common.Repositories;

import org.vedantee.common.Exceptions.DatabaseCustomException;
import org.vedantee.common.Models.Entities.BlacklistEntity;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

@Repository
@EnableAutoConfiguration
public interface BlacklistRepository extends JpaRepository<BlacklistEntity, Long> {
    boolean existsByPhoneNumber(String phoneNumber) throws DatabaseCustomException;
    ArrayList<BlacklistEntity> findByPhoneNumberIn(ArrayList<String> phoneNumbers) throws DatabaseCustomException;
    void deleteByPhoneNumber(String phoneNumber);
}