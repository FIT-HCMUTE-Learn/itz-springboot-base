package com.base.auth.validation.impl;


import com.base.auth.constant.UserBaseConstant;
import com.base.auth.validation.NationType;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

public class NationTypeValidation implements ConstraintValidator<NationType, Integer> {
    private boolean allowNull;
    private static final List<Integer> VALID_VALUES = Arrays.asList(
            UserBaseConstant.NATION_KIND_PROVINCE,
            UserBaseConstant.NATION_KIND_DISTRICT,
            UserBaseConstant.NATION_KIND_COMMUNE
    );

    @Override
    public void initialize(NationType constraintAnnotation) { allowNull = constraintAnnotation.allowNull(); }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return allowNull;
        }
        return VALID_VALUES.contains(value);
    }
}
