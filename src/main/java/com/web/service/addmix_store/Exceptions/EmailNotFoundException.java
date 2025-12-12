package com.web.service.addmix_store.Exceptions;

public class EmailNotFoundException extends RuntimeException{
    public EmailNotFoundException(){
        super();
    }

    public EmailNotFoundException(String message){
        super(message);
    }
}