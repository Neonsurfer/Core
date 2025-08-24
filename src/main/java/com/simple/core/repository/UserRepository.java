package com.simple.core.repository;

import com.simple.core.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("""
            SELECT COUNT(u) > 0
            FROM User u
            JOIN u.cards c
            JOIN u.tokens t
            WHERE c.id = :cardId
            AND t.token = :userToken
            """)
    boolean validateUserTokenAndCardId(@Param("userToken") String userToken, @Param("cardId") Long cardId);
}
