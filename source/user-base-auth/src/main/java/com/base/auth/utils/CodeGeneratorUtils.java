package com.base.auth.utils;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Component
public class CodeGeneratorUtils {
    @PersistenceContext
    private EntityManager entityManager;

    public <T> String generateUniqueCode(Class<T> entityClass, String fieldName, Integer length) {
        String code;
        do {
            code = RandomStringUtils.randomAlphanumeric(length);
        } while (isCodeExists(entityClass, fieldName, code));
        return code;
    }

    private <T> boolean isCodeExists(Class<T> entityClass, String fieldName, String code) {
        String queryStr = "SELECT COUNT(e) FROM " + entityClass.getSimpleName() + " e WHERE e." + fieldName + " = :code";
        TypedQuery<Long> query = entityManager.createQuery(queryStr, Long.class);
        query.setParameter("code", code);
        return query.getSingleResult() > 0;
    }
}
