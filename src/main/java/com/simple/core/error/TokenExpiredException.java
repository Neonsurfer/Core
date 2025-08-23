package com.simple.core.error;

public class TokenExpiredException extends BusinessException {
    public TokenExpiredException() {
        super("A felhasználói token lejárt vagy nem értelmezhető", 10051);
    }
}
