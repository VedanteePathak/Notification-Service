package org.adrij.consumer.Services;

public interface ThirdPartyAPIServiceAdapter {
    void sendSMSOkHttp(String phoneNumber, long requestId, String message);
    void sendSMSRestTemplate(String phoneNumber, long requestId, String smsText);
}