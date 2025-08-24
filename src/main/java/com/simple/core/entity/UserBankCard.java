package com.simple.core.entity;

import com.simple.core.util.EntityManagerProvider;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "UserBankCard")
public class UserBankCard {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "userId", nullable = false)
    private User user;
    @Id
    @Column(name = "cardId", nullable = false)
    private String cardId;
    @Column(name = "cardNumber", nullable = false, unique = true)
    private String cardNumber;
    @Column(name = "cvc", nullable = false)
    private String cvc;
    @Column(name = "name")
    private String name;
    @Column(name = "amount")
    private BigDecimal amount;
    @Column(name = "currency")
    private String currency;

    @PrePersist
    public void generateCardId() {
        if (this.user.getUserId() == null) {
            EntityManager em = EntityManagerProvider.getEntityManager();
            Long nextVal = (Long) em.createNativeQuery("SELECT NEXT VALUE FOR card_id_seq").getSingleResult();

            this.cardId = String.format("C%04d", nextVal);
        }
    }
}
