package com.base.auth.validation;

import com.base.auth.validation.impl.AddressTypeValidation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AddressTypeValidation.class)
@Documented
public @interface AddressType {
    boolean allowNull() default false;
    String message() default "Invalid address type. Allowed values: 1 (home), 2 (office)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
