package io.buzzy.sso.registration.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;
import org.passay.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;


public class PasswordConstraintsValidator implements ConstraintValidator<ValidPassword, String> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordConstraintsValidator.class);

    private static int MIN_LENGTH = 8;
    private static int MAX_LENGTH = 20;
    private static int MIN_CHARACTER_PER_TYPE = 1;

    private final PasswordValidator passwordValidator = new PasswordValidator(messageResolver(), passwordRules());

    @Override
    public void initialize(ValidPassword annotation) {
        MIN_LENGTH = annotation.min();
        MAX_LENGTH = annotation.max();
        MIN_CHARACTER_PER_TYPE = annotation.minCharacterPerType();
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (StringUtils.isBlank(password)) {
            return false;
        }

        RuleResult result = passwordValidator.validate(new PasswordData(password));

        if (result.isValid()) {
            return true;
        }

        String errorMessage = passwordValidator.getMessages(result).stream().findFirst().orElse("Invalid password.");

        context.buildConstraintViolationWithTemplate(errorMessage)
                .addConstraintViolation()
                .disableDefaultConstraintViolation();

        return false;
    }

    private MessageResolver messageResolver() {
        Properties props = new Properties();

        try (InputStream in = PasswordConstraintsValidator.class.getClassLoader().getResourceAsStream("messages/passay-override.properties")) {
            props.load(in);
        } catch (IOException e) {
            LOGGER.warn("Unable to load properties file, fallback to default messages", e);
            return new PropertiesMessageResolver();
        }

        return new PropertiesMessageResolver(props);
    }

    private List<Rule> passwordRules() {
        return List.of(new LengthRule(MIN_LENGTH, MAX_LENGTH),
                new CharacterRule(EnglishCharacterData.UpperCase, MIN_CHARACTER_PER_TYPE),
                new CharacterRule(EnglishCharacterData.LowerCase, MIN_CHARACTER_PER_TYPE),
                new CharacterRule(EnglishCharacterData.Digit, MIN_CHARACTER_PER_TYPE),
                new CharacterRule(EnglishCharacterData.Special, MIN_CHARACTER_PER_TYPE),
                new WhitespaceRule());
    }

}
