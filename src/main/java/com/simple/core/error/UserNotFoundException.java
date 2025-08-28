package com.simple.core.error;

public class UserNotFoundException extends BusinessException {
    public UserNotFoundException() {
        super("A felhasználó nem található", 10054);
    }
}
