package io.buzzy.sso.registration.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;


public class NoWhitespaceConstraintValidator implements ConstraintValidator<NoWhitespace, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtils.isBlank(value)) {
            return true;
        }

        return value.matches("\\S+");
    }
}
