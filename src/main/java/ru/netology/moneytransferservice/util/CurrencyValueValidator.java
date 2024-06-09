package ru.netology.moneytransferservice.util;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CurrencyValueValidator implements ConstraintValidator<CurrencyValidator, String> {

    private CurrencyValidator annotation;

    @Override
    public void initialize(CurrencyValidator annotation) {
        this.annotation = annotation;
    }

    @Override
    public boolean isValid(String valueForValidation, ConstraintValidatorContext constraintValidatorContext) {

        boolean result = false;
        Object[] enumValues = this.annotation.enumClass().getEnumConstants();

        if (enumValues != null) {
            for (Object enumValue : enumValues) {
                if (valueForValidation.equals(enumValue.toString())
                        || (this.annotation.ignoreCase()
                        && valueForValidation.equalsIgnoreCase(enumValue.toString()))) {
                    result = true;
                    break;
                }
            }
        }

        return result;

    }

}
