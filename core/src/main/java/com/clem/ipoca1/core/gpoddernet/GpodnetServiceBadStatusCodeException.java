package com.clem.ipoca1.core.gpoddernet;

public class GpodnetServiceBadStatusCodeException extends GpodnetServiceException {
    int statusCode;

    public GpodnetServiceBadStatusCodeException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }


}
