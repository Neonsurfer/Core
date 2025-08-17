package com.simple.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "UserToken")
public class UserToken {

    @Column(name = "userId", nullable = false)
    Long userId;
    @Column(name = "token", nullable = false, unique = true)
    String token;
}
