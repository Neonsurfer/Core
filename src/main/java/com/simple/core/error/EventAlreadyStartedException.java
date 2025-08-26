package com.simple.core.error;

public class EventAlreadyStartedException extends BusinessException {
    public EventAlreadyStartedException() {
        super("Az esemény már elkezdődött, ezért nem lehet rá helyet foglalni!", 10053);
    }
}
