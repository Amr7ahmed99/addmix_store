package com.web.service.addmix_store.Exceptions;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // @ExceptionHandler({ UsernameNotFoundException.class, BadRequestException.class , BadCredentialsException.class})
    // public ResponseEntity<Map<String, String>> handleBadRequest(Exception e) {
    //     return buildBadReqErrResponse(e.getMessage());
    // }

    // @ExceptionHandler(Exception.class)
    // public ResponseEntity<Map<String, String>> handleGeneric(Exception e) {
    //     LoggerFactory.getLogger(GlobalExceptionHandler.class)
    //                  .error("Unhandled error: {}", e.getMessage());
    //     Map<String, String> error = new HashMap<>();
    //     error.put("error", "Something went wrong");
    //     return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    // }

    // private ResponseEntity<Map<String, String>> buildBadReqErrResponse(String message) {
    //     Map<String, String> err = new HashMap<>();
    //     err.put("message", message);
    //     return ResponseEntity.badRequest().body(err);
    // }

    @ExceptionHandler({BadRequestException.class, IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<Map<String, Object>> handleBadRequestExceptions(RuntimeException ex) {
        logException(ex, HttpStatus.BAD_REQUEST);
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Bad Request");
        response.put("message", ex.getMessage());
        response.put("code", "BAD_REQUEST");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler({UsernameNotFoundException.class, EntityNotFoundException.class})
    public ResponseEntity<Map<String, Object>> handleNotFoundExceptions(RuntimeException ex) {
        logException(ex, HttpStatus.NOT_FOUND);
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.NOT_FOUND.value());
        response.put("error", "Resource Not Found"); 
        response.put("message", ex.getMessage());
        response.put("code", "NOT_FOUND");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        logException(ex, HttpStatus.BAD_REQUEST);
        
        // collect all invalid fields
        Map<String, String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                    FieldError::getField,
                    fieldError -> fieldError.getDefaultMessage() != null ? 
                                fieldError.getDefaultMessage() : "Validation error",
                    (existing, replacement) -> existing + ", " + replacement
                ));

        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.BAD_REQUEST.value());
        response.put("error", "Validation Failed");
        response.put("message", "One or more fields are invalid");
        response.put("code", "VALIDATION_FAILED");
        response.put("fieldErrors", fieldErrors);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        log.error("Unhandled exception: ", ex);
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("error", "Internal Server Error");
        response.put("message", "An unexpected error occurred. Please try again later.");
        response.put("code", "INTERNAL_ERROR");
        
        if (isDevelopmentEnvironment()) {
            response.put("debugMessage", ex.getMessage());
        }
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        log.error("Runtime exception: ", ex);
        
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.put("error", "Internal Server Error");
        response.put("message", "error occurred. Please try again later.");
        response.put("code", "RUNTIME_ERROR");
        
        if (isDevelopmentEnvironment()) {
            response.put("debugMessage", ex.getMessage());
        }
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    private boolean isDevelopmentEnvironment() {
        String env = System.getProperty("spring.profiles.active", "development");
        return "development".equals(env) || "local".equals(env);
    }

    private void logException(Exception ex, HttpStatus status) {
        log.error("Exception handled - Status: {}, Message: {}, Exception: {}", 
                 status, ex.getMessage(), ex.getClass().getSimpleName(), ex);
    }

}
