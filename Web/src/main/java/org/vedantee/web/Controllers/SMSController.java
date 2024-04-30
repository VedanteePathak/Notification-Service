package org.adrij.web.Controllers;

import lombok.extern.slf4j.Slf4j;
import org.adrij.common.Models.DTOs.Requests.SMSRequestDTO;
import org.adrij.common.Models.DTOs.Responses.ResponseDTO;
import org.adrij.web.Services.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Pattern;

import static org.adrij.web.Constants.APIEndPoints.*;

@RestController
@RequestMapping(SMS_REQUEST_END_POINT)
@Validated
@Slf4j
public class SMSController {
    private final SMSServiceAdapter smsService;
    private final String LOGGING_PREFIX = "SMS Controller | ";

    @Autowired
    public SMSController(SMSServiceAdapter smsService) {
        this.smsService = smsService;
    }

    @PostMapping(SMS_REQUEST_POST)
    public ResponseEntity<ResponseDTO> sendSMS(
            @Valid
            @RequestBody SMSRequestDTO smsRequestDTO,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String clientId) {
        log.info("{}Send SMS | Parameter | Client Id: = {} | SMS Request DTO: Phone Number = {}, Message = {}",
                LOGGING_PREFIX, clientId, smsRequestDTO.getPhoneNumber(), smsRequestDTO.getMessage());
        return smsService.sendSMS(smsRequestDTO, clientId);
    }

    @GetMapping(SMS_REQUEST_GET)
    public ResponseEntity<ResponseDTO> getSMSByRequestId(
            @Valid
            @Digits(integer = Integer.MAX_VALUE, fraction = 0, message = "Request ID must be a valid Integer or Long.")
            @PathVariable String requestId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String clientId) {
        log.info("{}Get SMS by Request Id | Parameter | Client Id = {} | Request Id = {}",
                LOGGING_PREFIX, clientId, requestId);
        return smsService.getSMSbyRequestId(requestId, clientId);
    }
}
