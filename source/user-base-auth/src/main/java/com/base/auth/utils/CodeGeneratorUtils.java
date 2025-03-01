package com.base.auth.utils;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Component
public class CodeGeneratorUtils {
    @PersistenceContext
    private EntityManager entityManager;

    public <T> String generateCode(Integer length) {
        return RandomStringUtils.randomAlphanumeric(length);
    }
}
