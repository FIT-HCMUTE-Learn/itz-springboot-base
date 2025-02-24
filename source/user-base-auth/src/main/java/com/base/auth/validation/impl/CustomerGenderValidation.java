package com.base.auth.validation.impl;

import com.base.auth.constant.UserBaseConstant;
import com.base.auth.validation.CustomerGender;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

public class CustomerGenderValidation implements ConstraintValidator<CustomerGender, Integer> {
    private boolean allowNull;
    private static final List<Integer> VALID_VALUES = Arrays.asList(
            UserBaseConstant.CUSTOMER_GENDER_UNKNOWN,
            UserBaseConstant.CUSTOMER_GENDER_MALE,
            UserBaseConstant.CUSTOMER_GENDER_FEMALE
    );

    @Override
    public void initialize(CustomerGender constraintAnnotation) { allowNull = constraintAnnotation.allowNull(); }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return allowNull;
        }
        return VALID_VALUES.contains(value);
    }
}
