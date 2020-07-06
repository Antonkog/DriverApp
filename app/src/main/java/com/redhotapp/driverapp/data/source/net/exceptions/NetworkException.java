package com.redhotapp.driverapp.data.source.net.exceptions;

public class NetworkException extends RuntimeException {

    private String message;

    public NetworkException(String message) {
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
