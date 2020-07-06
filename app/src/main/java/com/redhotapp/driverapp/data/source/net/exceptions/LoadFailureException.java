package com.redhotapp.driverapp.data.source.net.exceptions;

public class LoadFailureException extends RuntimeException {

    private String message;

    public LoadFailureException(String message) {
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
