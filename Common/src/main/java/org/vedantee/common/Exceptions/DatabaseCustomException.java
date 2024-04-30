package org.vedantee.common.Exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DatabaseCustomException extends CustomException {
    public DatabaseCustomException(String errorCode, String message) {
        super(errorCode, message);
    }

    public DatabaseCustomException(String message) {
        super("DATABASE_ERROR", message); // Default errorCode
    }
}
