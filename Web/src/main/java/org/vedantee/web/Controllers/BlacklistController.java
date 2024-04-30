package org.vedantee.web.Controllers;

import org.vedantee.common.Models.DTOs.Requests.BlacklistRequestDTO;

import org.vedantee.web.Services.Implementations.BlacklistService;
import org.vedantee.web.Services.BlacklistServiceAdapter;

import static org.vedantee.web.Constants.APIEndPoints.BLACKLIST_BATCHES_END_POINT;
import static org.vedantee.web.Constants.APIEndPoints.BLACKLIST_END_POINT;

import lombok.extern.slf4j.Slf4j;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.vedantee.common.Models.DTOs.Responses.ResponseDTO;

@RestController
@RequestMapping(BLACKLIST_END_POINT)
@Slf4j
@Validated
public class BlacklistController {
    private final BlacklistServiceAdapter blacklistService;
    private final String LOGGING_PREFIX = "Blacklist Controller | ";

    @Autowired
    public BlacklistController(BlacklistService blacklistService) {
        this.blacklistService = blacklistService;
    }

    @PostMapping()
    public ResponseEntity<ResponseDTO> addPhoneNumbersToBlacklist(
            @Valid
            @RequestBody
            BlacklistRequestDTO blacklistRequestDTO) {
        log.info("{}Add Phone Numbers to Blacklist | RequestDTO: {}",
                LOGGING_PREFIX, blacklistRequestDTO);
        return blacklistService.addPhoneNumbersToBlacklist(blacklistRequestDTO);
    }

    @PostMapping(BLACKLIST_BATCHES_END_POINT)
    public ResponseEntity<ResponseDTO> addPhoneNumbersToBlacklistInBatches(
            @Valid
            @RequestBody
            BlacklistRequestDTO blacklistRequestDTO) {
        log.info("{}Add Phone Numbers to Blacklist | RequestDTO: {}",
                LOGGING_PREFIX, blacklistRequestDTO);
        return blacklistService.addPhoneNumbersToBlacklistInBatches(blacklistRequestDTO);
    }

    @DeleteMapping()
    public ResponseEntity<ResponseDTO> removePhoneNumbersFromBlacklist(
            @Valid
            @RequestBody
            BlacklistRequestDTO blacklistRequestDTO) {
        log.info("{}Remove Phone Numbers from Blacklist in Batches | Blacklist Request DTO: Phone Numbers = {}",
                LOGGING_PREFIX, blacklistRequestDTO.getPhoneNumbers());
        return blacklistService.removePhoneNumbersFromBlacklist(blacklistRequestDTO);
    }

    @DeleteMapping(BLACKLIST_BATCHES_END_POINT)
    public ResponseEntity<ResponseDTO> removePhoneNumbersFromBlacklistInBatches(
            @Valid
            @RequestBody
            BlacklistRequestDTO blacklistRequestDTO) {
        log.info("{}Remove Phone Numbers from Blacklist in Batches | Blacklist Request DTO: Phone Numbers = {}",
                LOGGING_PREFIX, blacklistRequestDTO.getPhoneNumbers());
        return blacklistService.removePhoneNumbersFromBlacklistInBatches(blacklistRequestDTO);
    }

    @GetMapping()
    public ResponseEntity<ResponseDTO> getAllPhoneNumbersInBlacklist() {
        log.info("{}Get All Phone Numbers in Blacklist", LOGGING_PREFIX);
        return blacklistService.getAllPhoneNumbersInBlacklist();
    }
}