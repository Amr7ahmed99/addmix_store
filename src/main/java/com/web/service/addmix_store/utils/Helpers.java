package com.web.service.addmix_store.utils;

import java.util.Random;

public class Helpers {
    
    public static String generateVerificationCode() {
        return String.valueOf(new Random().nextInt(999999 - 100000) + 100000);
    }
}
