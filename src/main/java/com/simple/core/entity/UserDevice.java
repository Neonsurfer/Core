package com.simple.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "UserDevice")
public class UserDevice {

    @Column(name = "userId", nullable = false)
    Long userId;
    @Column(name = "deviceHash", nullable = false, unique = true)
    String deviceHash;
}
