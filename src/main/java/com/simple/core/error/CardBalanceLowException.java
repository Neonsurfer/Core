package com.simple.core.error;

public class CardBalanceLowException extends BusinessException {
    public CardBalanceLowException() {
        super("A felhasználónak nincs elegendő pénze hogy megvásárolja a jegyet!", 10101);
    }
}
