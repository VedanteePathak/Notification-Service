package org.vedantee.common.Exceptions;

import lombok.*;

@Getter
@Setter
public class ThirdPartyAPICustomException extends CustomException {
    public ThirdPartyAPICustomException(String errorCode, String message) {
        super(errorCode, message);
    }
}