package com.girigiri.kwrental.asset.equipment.controller.validator;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = TrimmedSizeValidator.class)
@Documented
public @interface TrimmedSize {
    String message() default "trimmed size validate!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int min() default 0;

    int max() default Integer.MAX_VALUE;
}

class TrimmedSizeValidator implements ConstraintValidator<TrimmedSize, String> {
    private int min;
    private int max;

    @Override
    public void initialize(TrimmedSize constraint) {
        min = constraint.min();
        max = constraint.max();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        String trimmedValue = value.trim();
        return trimmedValue.length() >= min && trimmedValue.length() <= max;
    }
}
