package com.clem.ipocadonation1.core.event;

import android.support.annotation.Nullable;

public class MessageEvent {

    public final String message;

    @Nullable
    public final Runnable action;

    public MessageEvent(String message) {
        this(message, null);
    }

    public MessageEvent(String message, Runnable action) {
        this.message = message;
        this.action = action;
    }

}
