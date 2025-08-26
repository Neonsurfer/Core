package com.simple.core.error;

public class SeatNotFoundException extends BusinessException {
    public SeatNotFoundException() {
        super("A kért szék nem található, vagy már nem lehet rá helyet foglalni", 10051);
    }
}
