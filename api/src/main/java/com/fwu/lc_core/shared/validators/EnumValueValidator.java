package com.fwu.lc_core.shared.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
public class EnumValueValidator implements
        ConstraintValidator<EnumValue, String> {

    private Set<String> allowedValues;

    @Override
    public void initialize(EnumValue constraintAnnotation) {
        Class<? extends Enum<?>> enumClass = constraintAnnotation.enumClass();
        allowedValues = Arrays.stream(enumClass.getEnumConstants())
                .map(e -> ((HasValue) e).value)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // TODO: Log invalid
        return allowedValues.contains(value);
    }
}
