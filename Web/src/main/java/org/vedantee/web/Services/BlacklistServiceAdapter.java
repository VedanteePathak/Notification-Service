package org.vedantee.web.Services;

import org.vedantee.common.Models.DTOs.Requests.BlacklistRequestDTO;
import org.vedantee.common.Models.DTOs.Responses.ResponseDTO;
import org.springframework.http.ResponseEntity;

public interface BlacklistServiceAdapter {
    ResponseEntity<ResponseDTO> addPhoneNumbersToBlacklist(BlacklistRequestDTO blacklistRequestDTO);
    ResponseEntity<ResponseDTO> removePhoneNumbersFromBlacklist(BlacklistRequestDTO blacklistRequestDTO);
    ResponseEntity<ResponseDTO> getAllPhoneNumbersInBlacklist();
    ResponseEntity<ResponseDTO> addPhoneNumbersToBlacklistInBatches(BlacklistRequestDTO blacklistRequestDTO);
    ResponseEntity<ResponseDTO> removePhoneNumbersFromBlacklistInBatches(BlacklistRequestDTO blacklistRequestDTO);
}
