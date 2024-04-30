package org.vedantee.common.Exceptions;

import lombok.*;

@Getter
@Setter
public class ElasticsearchCustomException extends CustomException {
    public ElasticsearchCustomException(String errorCode, String message) {
        super(errorCode, message);
    }

    public ElasticsearchCustomException(String message) {
        super("ELASTICSEARCH_ERROR", message);
    }
}
