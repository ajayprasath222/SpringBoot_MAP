package com.example.printer_springbe.product.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = FoodShiftsValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidFoodShifts {

    String message() default "Select 1 to 3 food shifts (MORNING, AFTERNOON, NIGHT)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
