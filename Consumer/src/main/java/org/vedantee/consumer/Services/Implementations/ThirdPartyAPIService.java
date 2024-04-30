package org.adrij.consumer.Services.Implementations;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.adrij.common.Exceptions.ThirdPartyAPICustomException;
import org.adrij.consumer.Services.ThirdPartyAPIServiceAdapter;

import com.google.gson.Gson;

import org.adrij.consumer.Services.Implementations.ThirdPartyAPISupports.Channels;
import org.adrij.consumer.Services.Implementations.ThirdPartyAPISupports.Destination;
import org.adrij.consumer.Services.Implementations.ThirdPartyAPISupports.SMS;
import org.adrij.consumer.Services.Implementations.ThirdPartyAPISupports.SMSBody;
import static org.adrij.consumer.Constants.ThirdPartyAPIConstants.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.MediaType;

import java.net.SocketTimeoutException;
import java.util.List;

@Service
@Slf4j
public class ThirdPartyAPIService implements ThirdPartyAPIServiceAdapter {
    private final RestTemplate restTemplate;

    private final OkHttpClient okHttpClient;

    @Autowired
    public ThirdPartyAPIService(RestTemplate restTemplate, OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
        this.restTemplate = restTemplate;
    }

    private final String LOGGING_PREFIX = "Third Party API Service | ";

    public void sendSMSOkHttp(String phoneNumber, long requestId, String smsText) {
        String SEND_SMS_OKHTTP_LOGGING_PREFIX = LOGGING_PREFIX + "Send SMS Ok Http | ";

        try {
            log.info("{}Parameters: PhoneNumber = {}, Request Id = {}, SMS Text = {}",
                    SEND_SMS_OKHTTP_LOGGING_PREFIX, phoneNumber, requestId, smsText);

            String smsRequestBodyJson = constructRequestBody(phoneNumber, requestId, smsText);

            RequestBody requestBody =
                    RequestBody.create(
                            smsRequestBodyJson,
                            okhttp3.MediaType.parse("application/json")
                    );

            log.info("{}Constructed Requests Json String = {}",
                    SEND_SMS_OKHTTP_LOGGING_PREFIX, requestBody);

            Request request = new Request.Builder()
                    .url(THIRD_PARTY_API_URL)
                    .post(requestBody)
                    .addHeader("accept", "application/json")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Key", THIRD_PARTY_API_KEY)
                    .build();

            log.info("{}Built Request = {}",
                    SEND_SMS_OKHTTP_LOGGING_PREFIX, request);

            Response response = okHttpClient.newCall(request).execute();

            log.info("{}Responses = {}",
                    SEND_SMS_OKHTTP_LOGGING_PREFIX, response);

            if (!response.isSuccessful()) {
                throw new ThirdPartyAPICustomException(
                        "API_CALL_FAILURE",
                        "Unexpected Responses code received while calling API. Exact: " + response);
            }
            if (response.body() == null) {
                throw new ThirdPartyAPICustomException(
                        "API_RESPONSE_EMPTY",
                        "Empty Responses received from API");
            }
            log.info("{}Third Party API Call success | API Response = {}",
                    SEND_SMS_OKHTTP_LOGGING_PREFIX, response);

        } catch (SocketTimeoutException e) {
            log.error("{}Exception Class = {} | Exception Stack Trace = {}",
                    SEND_SMS_OKHTTP_LOGGING_PREFIX, e.getClass(), e.getMessage());

            throw new ThirdPartyAPICustomException(
                    "API_TIMEOUT_ERROR",
                    String.format("Socket timeout occurred while calling third party API. Exception Class = %s. Exception Message = %s",
                            e.getClass(), e.getMessage())
            );

        } catch (ThirdPartyAPICustomException e) {
            log.error("{}Exception Class = {} | Exception Code = {} | Exception Message = {}",
                    SEND_SMS_OKHTTP_LOGGING_PREFIX, e.getClass(), e.getErrorCode(), e.getMessage());
            throw e;

        } catch (Exception e) {
            log.error("{}Exception Class = {} | Exception Message = {}",
                    SEND_SMS_OKHTTP_LOGGING_PREFIX, e.getClass(), e.getMessage());

            throw new ThirdPartyAPICustomException(
                    "API_UNKNOWN_ERROR",
                    "Unknown error happened which calling third party API. Exact: " + e.getMessage());
        }
    }

    public void sendSMSRestTemplate(String phoneNumber, long requestId, String smsText) {
        String SEND_SMS_REST_LOGGING_PREFIX = LOGGING_PREFIX + "Send SMS Rest Template | ";

        try {
            log.info("{}Parameters: Phone Number = {}, Request Id = {}, SMS Text = {}",
                    SEND_SMS_REST_LOGGING_PREFIX, phoneNumber, requestId, smsText);

            HttpHeaders headers = new HttpHeaders();
            headers.setBasicAuth(THIRD_PARTY_API_KEY);
            headers.setContentType(MediaType.APPLICATION_JSON);
            String smsRequestBodyJson = constructRequestBody(phoneNumber, requestId, smsText);

            log.info("{}Constructed Requests JSON String = {}",
                    SEND_SMS_REST_LOGGING_PREFIX, smsRequestBodyJson);

            HttpEntity<String> requestEntity = new HttpEntity<>(smsRequestBodyJson, headers);

            log.info("{}Request Entity = {}",
                    SEND_SMS_REST_LOGGING_PREFIX, requestEntity);
            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

            ResponseEntity<String> responseEntity =
                    restTemplate.exchange(
                            THIRD_PARTY_API_URL,
                            HttpMethod.POST,
                            requestEntity,
                            String.class
                    );

            log.info("{}After Call | API Responses Body = {} | API Responses Status Code = {}",
                    SEND_SMS_REST_LOGGING_PREFIX, responseEntity.getBody(), responseEntity.getStatusCode());

//            if (responseEntity.getStatusCode() == HttpStatus.OK) {
            if(responseEntity.getStatusCode().is2xxSuccessful()) {
                String responseBody = responseEntity.getBody();

                if (responseBody == null) {
                    throw new ThirdPartyAPICustomException(
                            "API_RESPONSE_EMPTY",
                            "Empty Responses received from API with status code " + responseEntity.getStatusCode());
                } else {
                    log.info("Third Party API Service | Send SMS Rest Template | Third Party API Call success | API Responses Status Code = {} | API Responses Body = {}", responseEntity.getStatusCode(), responseBody);
                }

            } else {
                throw new ThirdPartyAPICustomException(
                        "API_CALL_FAILURE",
                        "Empty Responses received from API | API Responses Body = " + responseEntity.getBody() + " | API Responses Status Code = "  + responseEntity.getStatusCode());
            }
        } catch (ResourceAccessException e) {
            log.error("{}Exception Class = {} | Exception Stack Trace = {}",
                    SEND_SMS_REST_LOGGING_PREFIX, e.getClass(), e.getStackTrace());

            throw new ThirdPartyAPICustomException(
                    "API_TIMEOUT_ERROR",
                    "Socket timeout occurred while calling third party API. Exact: " + e.getMessage());
        } catch (ThirdPartyAPICustomException e) {
            log.error("{}Exception Class = {} | Exception Code = {} | Exception Message = {}",
                    SEND_SMS_REST_LOGGING_PREFIX, e.getClass(), e.getErrorCode(), e.getMessage());

            throw e;
        } catch (Exception e) {
            log.error("{}Exception Class = {} | Exception Message = {}",
                    SEND_SMS_REST_LOGGING_PREFIX, e.getClass(), e.getMessage());

            throw new ThirdPartyAPICustomException(
                    "API_UNKNOWN_ERROR",
                    "Unknown error happened which calling third party API. Exact: " + e.getMessage());
        }
    }

    private String constructRequestBody(String phoneNumber, long requestId, String smsText) {
        String CONSTRUCT_REQUEST_LOGGING_PREFIX = LOGGING_PREFIX + "Construct Request Body | ";

        SMSBody smsRequestBody = new SMSBody();
        smsRequestBody.setDeliverychannel("sms");
        Channels channels = new Channels(new SMS(smsText));
        smsRequestBody.setChannels(channels);
        Destination destination = new Destination();
        destination.setCorrelationId(requestId);
        destination.setMsisdn(List.of(phoneNumber));
        smsRequestBody.setDestination(List.of(destination));

        log.info("{}Final Requests JSON in SMS Body Class format: {}",
                CONSTRUCT_REQUEST_LOGGING_PREFIX, smsRequestBody);

        Gson gson = new Gson();
        String smsRequestBodyJson = gson.toJson(smsRequestBody);

        log.info("{}Final Requests JSON in String format: {}",
                CONSTRUCT_REQUEST_LOGGING_PREFIX, smsRequestBody);

        return smsRequestBodyJson;
    }
}
