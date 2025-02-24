package com.base.auth.validation;

import com.base.auth.validation.impl.CustomerGenderValidation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CustomerGenderValidation.class)
@Documented
public @interface CustomerGender {
    boolean allowNull() default false;
    String message() default "Invalid gender. Allowed values: 0 (unknown), 1 (male), 2 (female)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
