package com.clem.ipocadonation1.core.gpoddernet;

public class GpodnetServiceBadStatusCodeException extends GpodnetServiceException {
    int statusCode;

    public GpodnetServiceBadStatusCodeException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }


}
