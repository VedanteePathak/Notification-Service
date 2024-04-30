package org.adrij.web.Services;

import org.adrij.common.Models.DTOs.Requests.SMSRequestDTO;
import org.adrij.common.Models.DTOs.Responses.ResponseDTO;
import org.springframework.http.ResponseEntity;

public interface SMSServiceAdapter {
    ResponseEntity<ResponseDTO> sendSMS(SMSRequestDTO smsRequestDTO, String clientId);
    ResponseEntity<ResponseDTO> getSMSbyRequestId(String requestId, String clientId);
}