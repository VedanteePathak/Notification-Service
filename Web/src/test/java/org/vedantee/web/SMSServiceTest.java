package org.adrij.web;

import org.adrij.common.Models.Entities.SMSEntity;
import org.adrij.common.Repositories.SMSRepository;
import org.adrij.common.Services.Implementations.RedisService;
import org.adrij.common.Models.DTOs.Responses.ResponseDTO;

import org.adrij.web.Services.Implementations.KafkaProducerService;
import org.adrij.web.Services.Implementations.SMSService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SMSServiceTest {
    @Mock
    private SMSRepository smsRepository;

    @Mock
    private KafkaProducerService kafkaService;

    @Mock
    private RedisService redisService;

    @InjectMocks
    private SMSService smsService;

//    @Test
//    public void testSendSMS_Success() {
//        // Mocking the RedisServiceAdapter to return false (indicating phone number not blacklisted)
//        when(redisService.contains(any())).thenReturn(false);
//
//        // Mocking the saveEntry method of SMSRequestDAO to do nothing (void method)
//        doNothing().when(smsRequestDAO).saveEntry(any(SMSRequest.class));
//
//        // Mocking the sendMessage method of KafkaService to accept any Long argument
//        doNothing().when(kafkaService).sendMessage(any());
//
//        // Creating a sample SMSRequestDTO
//        SMSRequestDTO smsRequestDTO = new SMSRequestDTO();
//        smsRequestDTO.setPhoneNumber("1234567890");
//        smsRequestDTO.setMessage("Test message");
//
//        // Calling the sendSMS method
//        ResponseEntity<ResponseDTO> responseEntity = smsService.sendSMS(smsRequestDTO);
//
//        // Verifying that the sendMessage method of KafkaService is called once
//        verify(kafkaService, times(1)).sendMessage(any());
//
//        // Verifying that the Responses entity is as expected
//        assertEquals(200, responseEntity.getStatusCodeValue());
//        assertEquals("Requests Successfully Sent",
//                ((SuccessResponseDTO<?>) Objects.requireNonNull(responseEntity.getBody())).getData());
//    }

    @Test
    public void testGetSMSByRequestId() {
        String requestId = "150";
        String clientId = "100";
        SMSEntity smsEntity = new SMSEntity();
        smsEntity.setRequestId(Long.parseLong(requestId));
        smsEntity.setPhoneNumber("1144880033");
        smsEntity.setMessage("Test message");

        when(smsRepository.findById(anyLong())).thenReturn(Optional.of(smsEntity));

        ResponseEntity<ResponseDTO> response = smsService.getSMSbyRequestId(requestId, clientId);
        assertEquals(200, response.getStatusCodeValue());
    }
}