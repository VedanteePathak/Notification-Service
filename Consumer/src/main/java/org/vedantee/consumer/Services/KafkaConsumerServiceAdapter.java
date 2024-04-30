package org.adrij.consumer.Services;

public interface KafkaConsumerServiceAdapter {
    void consumeKafkaMessageAndSendSMS(String kafkaReceivedMessage);
    boolean isPhoneNumberBlacklisted(String phoneNumber);
}