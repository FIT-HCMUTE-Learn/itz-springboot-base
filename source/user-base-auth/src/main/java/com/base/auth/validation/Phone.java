package com.base.auth.validation;

import com.base.auth.validation.impl.PhoneValidation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PhoneValidation.class)
@Documented
public @interface Phone {
    boolean allowNull() default false;
    String message() default "Invalid phone number. Must be 10-11 digits and start with 0";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
