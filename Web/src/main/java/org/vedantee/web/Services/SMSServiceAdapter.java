package org.vedantee.web.Services;

import org.vedantee.common.Models.DTOs.Requests.SMSRequestDTO;
import org.vedantee.common.Models.DTOs.Responses.ResponseDTO;
import org.springframework.http.ResponseEntity;

public interface SMSServiceAdapter {
    ResponseEntity<ResponseDTO> sendSMS(SMSRequestDTO smsRequestDTO, String clientId);
    ResponseEntity<ResponseDTO> getSMSbyRequestId(String requestId, String clientId);
}