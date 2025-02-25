package com.base.auth.validation.impl;

import com.base.auth.validation.Phone;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PhoneValidation implements ConstraintValidator<Phone, String> {
    private boolean allowNull;
    private static final String PHONE_REGEX = "0\\d{9,10}";

    @Override
    public void initialize(Phone constraintAnnotation) {
        this.allowNull = constraintAnnotation.allowNull();
    }

    @Override
    public boolean isValid(String phone, ConstraintValidatorContext context) {
        if (phone == null) {
            return allowNull;
        }
        return phone.matches(PHONE_REGEX);
    }
}
