package com.redhotapp.driverapp.data.source.net.exceptions;

public class EmptyDataException extends RuntimeException {

    private String message;

    public EmptyDataException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String errorMessage) {
        this.message = errorMessage;
    }

}
