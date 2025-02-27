package com.base.auth.validation.impl;

import com.base.auth.constant.UserBaseConstant;
import com.base.auth.validation.OrderState;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

public class OrderStateValidation implements ConstraintValidator<OrderState, Integer> {
    private boolean allowNull;
    private static final List<Integer> VALID_VALUES = Arrays.asList(
            UserBaseConstant.ORDER_STATE_BOOKING,
            UserBaseConstant.ORDER_STATE_APPROVED,
            UserBaseConstant.ORDER_STATE_DELIVERY,
            UserBaseConstant.ORDER_STATE_DONE,
            UserBaseConstant.ORDER_STATE_CANCEL
    );

    @Override
    public void initialize(OrderState constraintAnnotation) {
        allowNull = constraintAnnotation.allowNull();
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return allowNull;
        }
        return VALID_VALUES.contains(value);
    }
}
