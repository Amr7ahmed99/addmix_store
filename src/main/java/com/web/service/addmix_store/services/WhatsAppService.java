package com.web.service.addmix_store.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WhatsAppService {

    @Value("${whatsapp.api.url}")
    private String whatsappApiUrl;

    @Value("${whatsapp.api.token}")
    private String whatsappApiToken;

    public void sendOtp(String phoneNumber, String otp) {
        String egyPrefix= "2";
        // String jsonBody = """
        // {
        //     "messaging_product": "whatsapp",
        //     "to": "%s",
        //     "type": "template",
        //     "template": { 
        //         "name": "hello_world",
        //         "language": {"code": "en_US"},
        //     }
        // }
        // """.formatted("2"+phoneNumber, otp);

        String jsonBody = """
        {
            "messaging_product": "whatsapp",
            "to": "%s",
            "type": "template",
            "template": { 
                "name": "verify_otp",
                "language": {"code": "en_US"},
                "components": [{
                    "type": "body",
                    "parameters": [{
                        "type": "text",
                        "text": "%s"
                    }]
                }]
            }
        }
        """.formatted(egyPrefix + phoneNumber, otp);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(whatsappApiToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.postForEntity(whatsappApiUrl, request, String.class);
        
        // Log the response
        System.out.println("WhatsApp API Response: " + response.getBody());
        System.out.println("Status Code: " + response.getStatusCode());
    }
}

