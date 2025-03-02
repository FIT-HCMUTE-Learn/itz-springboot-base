package com.base.auth.validation.impl;

import com.base.auth.constant.UserBaseConstant;
import com.base.auth.validation.AddressType;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

public class AddressTypeValidation implements ConstraintValidator<AddressType, Integer> {
    private boolean allowNull;
    private static final List<Integer> VALID_VALUES = Arrays.asList(
            UserBaseConstant.CUSTOMER_ADDRESS_HOME,
            UserBaseConstant.CUSTOMER_ADDRESS_OFFICE
    );

    @Override
    public void initialize(AddressType constraintAnnotation) { allowNull = constraintAnnotation.allowNull(); }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return allowNull;
        }
        return VALID_VALUES.contains(value);
    }
}
