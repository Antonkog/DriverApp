package com.redhotapp.driverapp.data.source.net.exceptions;

public class AbonaHttpException extends RuntimeException {

    private int errorCode;
    private String message;


    public AbonaHttpException(int errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String errorMessage) {
        this.message = errorMessage;
    }

}
