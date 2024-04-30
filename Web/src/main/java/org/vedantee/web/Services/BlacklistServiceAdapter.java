package org.adrij.web.Services;

import org.adrij.common.Models.DTOs.Requests.BlacklistRequestDTO;
import org.adrij.common.Models.DTOs.Responses.ResponseDTO;
import org.springframework.http.ResponseEntity;

public interface BlacklistServiceAdapter {
    ResponseEntity<ResponseDTO> addPhoneNumbersToBlacklist(BlacklistRequestDTO blacklistRequestDTO);
    ResponseEntity<ResponseDTO> removePhoneNumbersFromBlacklist(BlacklistRequestDTO blacklistRequestDTO);
    ResponseEntity<ResponseDTO> getAllPhoneNumbersInBlacklist();
    ResponseEntity<ResponseDTO> addPhoneNumbersToBlacklistInBatches(BlacklistRequestDTO blacklistRequestDTO);
    ResponseEntity<ResponseDTO> removePhoneNumbersFromBlacklistInBatches(BlacklistRequestDTO blacklistRequestDTO);
}
