package org.vedantee.common.Exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KafkaCustomException extends CustomException {
    public KafkaCustomException(String errorCode, String message) {
        super(errorCode, message);
    }

    public KafkaCustomException(String message) {
        super("KAFKA_ERROR", message);
    }
}
