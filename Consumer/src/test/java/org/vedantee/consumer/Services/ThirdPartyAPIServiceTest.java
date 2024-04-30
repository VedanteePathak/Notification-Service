package org.vedantee.consumer.Services;

import io.netty.handler.codec.http.HttpHeaders;
import org.junit.jupiter.api.Test;
import org.vedantee.consumer.Services.Implementations.ThirdPartyAPIService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.vedantee.consumer.Constants.ThirdPartyAPIConstants.THIRD_PARTY_API_KEY;

@ExtendWith(MockitoExtension.class)
public class ThirdPartyAPIServiceTest {
    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ThirdPartyAPIService thirdPartyAPIService;

    @BeforeEach
    public void setup() {
//        this.thirdPartyAPIService = new ThirdPartyAPIService();
    }

//    @Test
//    public void sendSMSRestTemplate_Success() {
//    String phoneNumber = "1234567890";
//    String smsText = "Test SMS";
//    long requestId = 12345;
//
//        String expectedRequestBody
//                = "{\"deliverychannel\":\"sms\",\"channels\":{\"sms\":{\"text\":\"Test SMS\"}},\"destination\":[{\"correlationId\":12345,\"msisdn\":[\"1234567890\"]}]}";
//
//        HttpHeaders expectedHeaders = new HttpHeaders();
//        expectedHeaders.set("Key", THIRD_PARTY_API_KEY);
//        expectedHeaders.setContentType(MediaType.APPLICATION_JSON);
//
//        // Stub the restTemplate mock to return a successful ResponseEntity
//        ResponseEntity<String> responseEntity = new ResponseEntity<>("Responses body", HttpStatus.OK);
//
////        when(
////                restTemplate.exchange(
////                        eq(THIRD_PARTY_API_URL),
////                        eq(HttpMethod.POST),
////                        eq(new HttpEntity<>(expectedRequestBody, expectedHeaders)),
////                        eq(String.class)))
////                .thenReturn(responseEntity);
//
//        doReturn(responseEntity).when(restTemplate).exchange(
//                eq(THIRD_PARTY_API_URL),
//                eq(HttpMethod.POST),
//                eq(new HttpEntity<>(expectedRequestBody, expectedHeaders)),
//                eq(String.class)
//        );
//
//        // Call the method to be tested
//        thirdPartyAPIService.sendSMSRestTemplate();
//
//        // Verify that the restTemplate exchange method was called with the expected arguments
//        verify(restTemplate, times(2)).exchange(
//                eq(THIRD_PARTY_API_URL),
//                eq(HttpMethod.POST),
//                eq(new HttpEntity<>(expectedRequestBody, expectedHeaders)),
//                eq(String.class)
//        );
//    }
//
//    @Test
//    public void sendSMSRestTemplate_EmptyResponse() {
//        HttpHeaders expectedHeaders = new HttpHeaders();
//        expectedHeaders.set("Key", THIRD_PARTY_API_KEY);
//        expectedHeaders.setContentType(MediaType.APPLICATION_JSON);
//
//        ResponseEntity<String> responseEntity = new ResponseEntity<>(null, HttpStatus.OK);
//
//        when(restTemplate.exchange(
//                eq(THIRD_PARTY_API_URL),
//                eq(HttpMethod.POST),
//                ArgumentMatchers.<HttpEntity<String>>any(),
//                eq(String.class)))
//                .thenReturn(responseEntity);
//
//        assertThrows(ThirdPartyAPICustomException.class, () -> thirdPartyAPIService.sendSMSRestTemplate());
//    }
//
//    @Test
//    public void sendSMSRestTemplate_ApiCallFailure() {
//        String expectedRequestBody =
//                "{\"deliverychannel\":\"sms\",\"channels\":{\"sms\":{\"text\":\"Test SMS\"}}," +
//                "\"destination\":[{\"correlationId\":12345,\"msisdn\":[\"1234567890\"]}]}";
//
//        HttpHeaders expectedHeaders = new HttpHeaders();
//        expectedHeaders.set("Key", THIRD_PARTY_API_KEY);
//        expectedHeaders.setContentType(MediaType.APPLICATION_JSON);
//
//        doThrow(new RestClientException("Test RestClientException")).
//                when(
//                        restTemplate.exchange(
//                                eq(THIRD_PARTY_API_URL),
//                                eq(HttpMethod.POST),
//                                eq(new HttpEntity<>(expectedRequestBody, expectedHeaders)),
//                                eq(String.class)));
//
//        ThirdPartyAPICustomException exception =
//                assertThrows(ThirdPartyAPICustomException.class, () -> thirdPartyAPIService.sendSMSRestTemplate());
//
//        assertEquals("API_CALL_FAILURE", exception.getErrorCode());
//        assertEquals("Error occurred while calling the API. Exact: Test RestClientException", exception.getMessage());
//
//        verify(restTemplate, times(1)).exchange(
//                eq(THIRD_PARTY_API_URL),
//                eq(HttpMethod.POST),
//                eq(new HttpEntity<>(expectedRequestBody, expectedHeaders)),
//                eq(String.class));
//    }
}