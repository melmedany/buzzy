package io.buzzy.sso.registration.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PasswordConstraintsValidatorTest {

    private PasswordConstraintsValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder;

    @BeforeEach
    void setUp() {
        validator = new PasswordConstraintsValidator();
    }

    @Test
    void testValidPassword() {
        String validPassword = "Valid1@Password";
        assertTrue(validator.isValid(validPassword, context));
    }

    @Test
    void testInvalidPasswordTooShort() {
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
        when(violationBuilder.addConstraintViolation()).thenReturn(context);

        String invalidPassword = "Shrt1!";
        assertFalse(validator.isValid(invalidPassword, context));
    }

    @Test
    void testInvalidPasswordNoUpperCase() {
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
        when(violationBuilder.addConstraintViolation()).thenReturn(context);

        String invalidPassword = "nouppercase1@";
        assertFalse(validator.isValid(invalidPassword, context));
    }

    @Test
    void testInvalidPasswordNoLowerCase() {
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
        when(violationBuilder.addConstraintViolation()).thenReturn(context);

        String invalidPassword = "NOLOWERCASE1@";
        assertFalse(validator.isValid(invalidPassword, context));
    }

    @Test
    void testInvalidPasswordNoDigit() {
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
        when(violationBuilder.addConstraintViolation()).thenReturn(context);

        String invalidPassword = "NoDigit@Password";
        assertFalse(validator.isValid(invalidPassword, context));
    }

    @Test
    void testInvalidPasswordNoSpecialCharacter() {
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
        when(violationBuilder.addConstraintViolation()).thenReturn(context);

        String invalidPassword = "NoSpecial1Password";
        assertFalse(validator.isValid(invalidPassword, context));
    }

    @Test
    void testInvalidPasswordWithWhitespace() {
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);
        when(violationBuilder.addConstraintViolation()).thenReturn(context);

        String invalidPassword = "Invalid Password1@";
        assertFalse(validator.isValid(invalidPassword, context));
    }

    @Test
    void testBlankPassword() {
        String blankPassword = "";
        assertFalse(validator.isValid(blankPassword, context));
    }

    @Test
    void testNullPassword() {
        assertFalse(validator.isValid(null, context));
    }
}