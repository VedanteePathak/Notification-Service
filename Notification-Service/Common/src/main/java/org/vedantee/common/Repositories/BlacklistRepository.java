package org.adrij.common.Repositories;

import org.adrij.common.Exceptions.DatabaseCustomException;
import org.adrij.common.Models.Entities.BlacklistEntity;
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