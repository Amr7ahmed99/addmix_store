package com.web.service.addmix_store.Exceptions;

public class EntityNotFoundException extends RuntimeException{
    public EntityNotFoundException(){
        super();
    }

    public EntityNotFoundException(String message){
        super(message);
    }
}
