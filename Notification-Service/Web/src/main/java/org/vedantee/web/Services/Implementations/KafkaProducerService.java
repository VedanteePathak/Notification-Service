package org.adrij.web.Services.Implementations;

import lombok.extern.slf4j.Slf4j;
import org.adrij.common.Exceptions.KafkaCustomException;
import org.adrij.web.Services.KafkaProducerServiceAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static org.adrij.web.Constants.KafkaProducerConstants.PRODUCER_KAFKA_TOPIC_NAME;

@Component
@Slf4j
public class KafkaProducerService implements KafkaProducerServiceAdapter {
    private static final String TOPIC = PRODUCER_KAFKA_TOPIC_NAME;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void produceKafkaMessage(Long requestId) {
        try {
            kafkaTemplate.send(TOPIC, String.valueOf(requestId));
        } catch (Exception e) {
            log.error("Kafka Producer Service | Send Kafka Message | Exception Class = {} | Exception Message = {}",
                    e.getClass(), e.getMessage());
            throw new KafkaCustomException("Error in sending Kafka message. Exact: " + e.getMessage());
        }
    }
}

