package com.redhotapp.driverapp.data.source.net.exceptions;

public class NoNetworkException extends RuntimeException {

    public NoNetworkException() {
        super();
    }

    public NoNetworkException(String message) {
        super(message);
    }

    public NoNetworkException(String message, Throwable cause) {
        super(message, cause);
    }

}
