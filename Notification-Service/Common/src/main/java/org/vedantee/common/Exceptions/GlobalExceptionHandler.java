package org.adrij.common.Exceptions;

import lombok.extern.slf4j.Slf4j;
import org.adrij.common.Models.DTOs.Responses.ErrorDetails;
import org.adrij.common.Models.DTOs.Responses.ResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import javax.validation.ConstraintViolationException;
import java.util.List;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    private static final String LOGGIN_PREFIX = "Global Exception Handler | ";

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleGlobalException(Exception ex, WebRequest request) {
        log.info("{}Handle Global Exception | Exception: ",
                LOGGIN_PREFIX, ex);

        ResponseDTO errorMessage =
                new ResponseDTO<>(
                        null,
                        String.format("An unknown error occurred. Exception Class = %s, Exception Message = %s",
                                ex.getClass(),
                                ex.getMessage()
                        )
                );

        return ResponseEntity.internalServerError().body(errorMessage);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ResponseDTO> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.info("{}Handle Validation Exception | Exception: ",
                LOGGIN_PREFIX, ex);

        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();

        StringBuilder errorMessage = new StringBuilder();
        for (FieldError fieldError : fieldErrors) {
            errorMessage.append(fieldError.getDefaultMessage()).append("; ");
        }

        ResponseDTO errorResponse =
                new ResponseDTO<>(
                        null,
                        new ErrorDetails(
                                "INVALID_REQUEST_" + HttpStatus.BAD_REQUEST.value(),
                                ex.getClass(),
                                errorMessage.toString()
                        )
                );

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ResponseDTO> handleJsonParseException(HttpMessageNotReadableException ex) {
        log.info("{}Handle JSON Parse Exception | Exception: ",
                LOGGIN_PREFIX, ex);

        ResponseDTO errorResponse =
                new ResponseDTO<>(
                        null,
                        new ErrorDetails(
                                "INVALID_REQUEST_" + HttpStatus.BAD_REQUEST.value(),
                                ex.getClass(),
                                "Invalid JSON format. " + ex.getMessage()
                        )
                );

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ResponseDTO> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
        log.info("{}Handle Constraint Violation Exception | Exception: ",
                LOGGIN_PREFIX, ex);

        ResponseDTO errorResponse =
                new ResponseDTO<>(
                        null,
                        new ErrorDetails(
                                "INVALID_REQUEST_NUMBER_CONVERSION_ERROR_" + HttpStatus.BAD_REQUEST.value(),
                                ex.getClass(),
                                ex.getMessage()
                        )
                );

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(RequestNotFoundCustomException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ResponseDTO> handleRequestNotFoundCustomException(RequestNotFoundCustomException ex, WebRequest request) {
        log.info("{}Handle Request Not Found Custom Exception | Exception: ",
                LOGGIN_PREFIX, ex);

        ResponseDTO errorResponse =
                new ResponseDTO<>(
                        null,
                        new ErrorDetails(
                                "INVALID_REQUEST_NUMBER_CONVERSION_ERROR_" + HttpStatus.BAD_REQUEST.value(),
                                ex.getClass(),
                                ex.getMessage()
                        )
                );

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(CustomException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ResponseDTO> handleCustomException(CustomException ex, WebRequest request) {
        log.info("{}Handle Custom Exception | Exception: ",
                LOGGIN_PREFIX, ex);

        ResponseDTO errorResponse =
                new ResponseDTO(
                        null,
                        new ErrorDetails(
                                ex.getErrorCode(),
                                ex.getClass(),
                                ex.getMessage()
                )
        );

        return ResponseEntity.internalServerError().body(errorResponse);
    }
}