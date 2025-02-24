package com.base.auth.validation;


import com.base.auth.validation.impl.NationTypeValidation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NationTypeValidation.class)
@Documented
public @interface NationType {
    boolean allowNull() default false;
    String message() default "Invalid nation type. Allowed values: 0 (province), 1 (district), 2 (commune)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
