package com.simple.core.error;

public class UserBankCardMismatchException extends BusinessException {
    public UserBankCardMismatchException() {
        super("Ez a bankkártya nem ehhez a felhasználóhoz tartozik", 10100);
    }
}
