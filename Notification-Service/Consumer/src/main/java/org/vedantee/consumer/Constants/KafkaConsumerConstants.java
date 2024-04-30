package org.adrij.consumer.Constants;

public class KafkaConsumerConstants {
    public static final String CONSUMER_BOOTSTRAP_SERVERS = "localhost:9092";
    public static final String CONSUMER_GROUP_ID = "notification-consumer";
    public static final String AUTO_OFFSET_RESET = "earliest";
    public static final String KEY_DESERIALIZER_CLASS = "org.apache.kafka.common.serialization.StringDeserializer";
    public static final String VALUE_DESERIALIZER_CLASS = "org.apache.kafka.common.serialization.StringDeserializer";
    public static final String CONSUMER_KAFKA_TOPIC_NAME = "notification.send_sms";
}
