package pl.sonmiike.financeapiservice.validators.password;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,}$");
    // Minimum eight characters, at least one uppercase letter, one lowercase letter, and one number

    @Override
    public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }
}
