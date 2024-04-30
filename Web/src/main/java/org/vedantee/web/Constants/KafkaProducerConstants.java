package org.vedantee.web.Constants;

public class KafkaProducerConstants {
    public static final String PRODUCER_KAFKA_TOPIC_NAME = "notification.send_sms";
    public static final String PRODUCER_BOOTSTRAP_SERVERS = "localhost:9092";
    public static final String KEY_SERIALIZER_CLASS = "org.apache.kafka.common.serialization.StringSerializer";
    public static final String VALUE_SERIALIZER_CLASS = "org.apache.kafka.common.serialization.StringSerializer";
}