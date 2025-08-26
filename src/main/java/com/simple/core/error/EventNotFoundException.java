package com.simple.core.error;

public class EventNotFoundException extends BusinessException {
    public EventNotFoundException() {
        super("A kért esemény nem található", 10052);
    }
}
