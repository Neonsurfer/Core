package com.simple.core.repository;

import com.simple.core.entity.UserBankCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

public interface UserBankCardRepository extends JpaRepository<UserBankCard, String> {

    @Modifying
    @Query("""
            UPDATE UserBankCard
            SET amount = :newAmount
            WHERE cardId = :cardId
            """)
    void updateBalance(@Param("cardId") String cardId, @Param("newAmount") BigDecimal newAmount);
}
