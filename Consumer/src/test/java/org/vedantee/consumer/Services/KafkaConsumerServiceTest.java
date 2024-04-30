package org.vedantee.consumer.Services;

import org.vedantee.common.Exceptions.RedisCustomException;
import org.vedantee.common.Models.Entities.ElasticSMSDocument;
import org.vedantee.common.Models.Entities.SMSEntity;
import org.vedantee.common.Repositories.SMSRepository;

import org.vedantee.common.Services.ElasticsearchServiceAdapter;
import org.vedantee.common.Services.RedisServiceAdapter;
import org.vedantee.consumer.Services.Implementations.KafkaConsumerService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KafkaConsumerServiceTest {
    @Mock
    private SMSRepository smsRequestDAO;

    @Mock
    private RedisServiceAdapter redisService;

    @Mock
    private ElasticsearchServiceAdapter elasticsearchService;

    @Mock
    private ThirdPartyAPIServiceAdapter thirdPartyAPIService;

    @InjectMocks
    private KafkaConsumerService kafkaConsumerService;

    @Test
    public void testConsumeMessage() {
        long requestId = 12345L;
        String phoneNumber = "1234567890";
        SMSEntity smsRequest = new SMSEntity();
        smsRequest.setRequestId(requestId);
        smsRequest.setPhoneNumber(phoneNumber);
        when(smsRequestDAO.findById(requestId)).thenReturn(Optional.of(smsRequest));
        when(redisService.contains(phoneNumber)).thenReturn(false);

        kafkaConsumerService.consumeKafkaMessageAndSendSMS(Long.toString(requestId));

        verify(smsRequestDAO).findById(requestId);
        verify(redisService).contains(phoneNumber);
        verify(thirdPartyAPIService).sendSMSRestTemplate(phoneNumber, requestId, smsRequest.getMessage());
        verify(smsRequestDAO).save(smsRequest);
        verify(elasticsearchService).saveToElasticsearch(any(ElasticSMSDocument.class));
    }

    @Test
    public void testConsumeMessage_redisServiceException() {
        // Mock the behavior of dependencies
        long requestId = 12345L;
        String phoneNumber = "1234567890";
        SMSEntity smsRequest = new SMSEntity();
        smsRequest.setRequestId(requestId);
        smsRequest.setPhoneNumber(phoneNumber);

        doReturn(Optional.of(smsRequest)).when(smsRequestDAO).findByRequestId(anyLong());
        doReturn(false).when(redisService).contains(anyString());
        doThrow(new RedisCustomException("REDIS_ERROR", "Error occurred while checking phone number in Redis."))
                .when(redisService).contains(anyString());

        // Call the method under test and assert the exception
        RedisCustomException exception
                = assertThrows(RedisCustomException.class, () -> kafkaConsumerService.consumeKafkaMessageAndSendSMS(Long.toString(requestId)));

        // Verify the interactions and assertions
        verify(smsRequestDAO).findByRequestId(requestId);
        verify(redisService).contains(phoneNumber);
        assertEquals("REDIS_ERROR", exception.getErrorCode());
        assertEquals("Error occurred while checking phone number in Redis.", exception.getMessage());
        verify(thirdPartyAPIService, never()).sendSMSRestTemplate(anyString(), anyLong(), anyString());
        verify(smsRequestDAO, never()).save(any());
        verify(elasticsearchService, never()).saveToElasticsearch(any());
    }

}
