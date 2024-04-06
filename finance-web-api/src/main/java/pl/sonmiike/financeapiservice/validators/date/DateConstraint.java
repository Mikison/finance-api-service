package pl.sonmiike.financeapiservice.validators.date;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target({ElementType.METHOD, ElementType.FIELD})
@Constraint(validatedBy = DateValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface DateConstraint {

    String message() default "Invalid date format";


    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

}
