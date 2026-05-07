package com.maceloaraujo.AgroManager.API.exception;

public class ResourceNotFoundException extends  RuntimeException {
    public ResourceNotFoundException(String mensage){
        super(mensage);
    }
}
