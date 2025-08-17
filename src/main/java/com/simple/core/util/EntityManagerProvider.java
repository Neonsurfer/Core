package com.simple.core.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.Getter;


public class EntityManagerProvider {

    @Getter
    private static EntityManager entityManager;

    @PersistenceContext
    public void init(EntityManager em) {
        entityManager = em;
    }

}
