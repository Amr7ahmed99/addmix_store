package com.web.service.addmix_store.Exceptions;

public class MobileNotFoundException extends RuntimeException{
    public MobileNotFoundException(){
        super();
    }

    public MobileNotFoundException(String message){
        super(message);
    }
}