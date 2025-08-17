package com.simple.core.entity;

import com.simple.core.util.EntityManagerProvider;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "UserBankCard")
public class UserBankCard {

    @Column(name = "userId", nullable = false)
    Long userId;
    @Id
    @Column(name = "cardId", nullable = false)
    String cardId;
    @Column(name = "cardNumber", nullable = false, unique = true)
    String cardNumber;
    @Column(name = "cvc", nullable = false)
    String cvc;
    @Column(name = "name")
    String name;
    @Column(name = "amount")
    BigDecimal amount;
    @Column(name = "currency")
    String currency;

    @PrePersist
    public void generateCardId() {
        if (this.userId == null) {
            EntityManager em = EntityManagerProvider.getEntityManager();
            Long nextVal = (Long) em.createNativeQuery("SELECT NEXT VALUE FOR card_id_seq").getSingleResult();

            this.cardId = String.format("C%04d", nextVal);
        }
    }
}
