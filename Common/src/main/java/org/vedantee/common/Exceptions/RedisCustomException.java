package org.vedantee.common.Exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RedisCustomException extends CustomException {
    public RedisCustomException(String errorCode, String message) {
        super(errorCode, message);
    }

    public RedisCustomException(String message) {
        super("REDIS_ERROR", message);
    }
}