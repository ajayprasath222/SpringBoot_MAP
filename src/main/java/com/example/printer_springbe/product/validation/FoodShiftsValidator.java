package com.example.printer_springbe.product.validation;

import com.example.printer_springbe.product.enums.FoodShift;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Set;

public class FoodShiftsValidator implements ConstraintValidator<ValidFoodShifts, Set<FoodShift>> {

    @Override
    public boolean isValid(Set<FoodShift> value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return false;
        }
        if (value.size() > 3) {
            return false;
        }
        return value.size() == value.stream().distinct().count();
    }
}
