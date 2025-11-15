package com.web.service.addmix_store.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import com.web.service.addmix_store.dtos.ProductDTO;

public class Helpers {
    
    public static String generateVerificationCode() {
        return String.valueOf(new Random().nextInt(999999 - 100000) + 100000);
    }

    public ResponseEntity<Map<String, String>> buildBadReqErrResponse(String message) {
        Map<String, String> err = new HashMap<>();
        err.put("message", message);
        return ResponseEntity.badRequest().body(err);
    }

    /**
     * Helper method to format paginated response
     */
    public static Map<String, Object> buildResponse(Page<ProductDTO> page) {
        Map<String, Object> response = new HashMap<>();
        response.put("content", page.getContent());
        response.put("page", page.getNumber());
        response.put("size", page.getSize());
        response.put("totalElements", page.getTotalElements());
        response.put("totalPages", page.getTotalPages());
        response.put("last", page.isLast());
        return response;
    }
}
