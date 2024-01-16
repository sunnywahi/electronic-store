package com.sample.electronicstore.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final static Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<?> handleDataIntegrityViolationException(final DataAccessException ex, final WebRequest request) {
        logger.info("for request {} have experienced following exception", request, ex);
        String errorMessage = "Data violation: " + ex.getMostSpecificCause().getMessage();
        return new ResponseEntity<>(errorMessage, HttpStatus.CONFLICT);
    }
}
