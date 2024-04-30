package org.vedantee.common.Exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestNotFoundCustomException extends CustomException {
    public RequestNotFoundCustomException(String errorCode, String message) {
        super(errorCode, message);
    }

    public RequestNotFoundCustomException(String message) {
        super("REQUEST_NOT_FOUND", message);
    }
}
