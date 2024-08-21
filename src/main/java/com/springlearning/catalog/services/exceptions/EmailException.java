package com.springlearning.catalog.services.exceptions;


public class EmailException extends RuntimeException {

    public EmailException(String msg) {
        super(msg);
    }
}