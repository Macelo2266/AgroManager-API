package com.maceloaraujo.AgroManager.API.exception;

public class BusinessException extends RuntimeException{
    public BusinessException(String mensage){
        super(mensage);
    }
}
