package com.simple.core.error;

public class TokenNotFoundException extends BusinessException {
    public TokenNotFoundException() {
        super("A felhasználói token nem szerepel", 10050);
    }
}
