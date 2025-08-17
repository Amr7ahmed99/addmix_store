package com.web.service.addmix_store.Exceptions;

import java.util.HashMap;
import java.util.Map;

import org.apache.coyote.BadRequestException;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ UsernameNotFoundException.class, BadRequestException.class , BadCredentialsException.class})
    public ResponseEntity<Map<String, String>> handleBadRequest(Exception e) {
        return buildBadReqErrResponse(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneric(Exception e) {
        LoggerFactory.getLogger(GlobalExceptionHandler.class)
                     .error("Unhandled error: {}", e.getMessage());
        return buildBadReqErrResponse("Something went wrong, please try again later");
    }

    private ResponseEntity<Map<String, String>> buildBadReqErrResponse(String message) {
        Map<String, String> err = new HashMap<>();
        err.put("message", message);
        return ResponseEntity.badRequest().body(err);
    }
}
