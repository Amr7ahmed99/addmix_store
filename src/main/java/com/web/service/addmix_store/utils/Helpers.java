package com.web.service.addmix_store.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.http.ResponseEntity;

public class Helpers {
    
    public static String generateVerificationCode() {
        return String.valueOf(new Random().nextInt(999999 - 100000) + 100000);
    }

    public ResponseEntity<Map<String, String>> buildBadReqErrResponse(String message) {
    Map<String, String> err = new HashMap<>();
    err.put("message", message);
    return ResponseEntity.badRequest().body(err);
}

}
