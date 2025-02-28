package com.base.auth.validation;

import com.base.auth.validation.impl.OrderStateValidation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = OrderStateValidation.class)
@Documented
public @interface OrderState {
    boolean allowNull() default false;
    String message() default "Invalid product state. Allowed values: 1 (booking), 2 (approved), 3 (delivery), 4 (done), 5 (cancel)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

