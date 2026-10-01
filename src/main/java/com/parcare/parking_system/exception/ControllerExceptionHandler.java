package com.parcare.parking_system.exception;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Order(Ordered.HIGHEST_PRECEDENCE + 1)
@ControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.toList());

        ApiExceptionResponse response = ApiExceptionResponse.builder()
                .message("Datele introduse nu sunt valide!")
                .status(HttpStatus.BAD_REQUEST)
                .errors(errors)
                .build();

        return responseEntityBuilder(response);
    }

    @ExceptionHandler(value = ApiExceptionResponse.class)
    protected ResponseEntity<Object> handleApiExceptionResponse(ApiExceptionResponse ex) {
        HttpStatus status = ex.getStatus() != null ? ex.getStatus() : HttpStatus.INTERNAL_SERVER_ERROR;
        return responseEntityBuilder(ApiExceptionResponse.builder()
                .errors(ex.getErrors())
                .status(status)
                .message(ex.getMessage())
                .build());
    }

    @ExceptionHandler(value = {NoSuchElementException.class, IllegalStateException.class, IllegalArgumentException.class})
    protected ResponseEntity<Object> handleStandardExceptions(Exception ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        if (ex instanceof NoSuchElementException) {
            status = HttpStatus.NOT_FOUND;
        }

        ApiExceptionResponse response = ApiExceptionResponse.builder()
                .message(ex.getMessage())
                .status(status)
                .errors(Collections.singletonList(ex.getClass().getSimpleName()))
                .build();
        
        return responseEntityBuilder(response);
    }

    private ResponseEntity<Object> responseEntityBuilder(ApiExceptionResponse ex) {
        return new ResponseEntity<>(ex, ex.getStatus());
    }
}

