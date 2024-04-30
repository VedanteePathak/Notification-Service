package org.vedantee.common.Models.DTOs.Requests.Validators;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class PhoneNumberValidator implements ConstraintValidator<PhoneNumberConstraint, ArrayList<String>> {
    private static final Pattern VALID_PHONE_NUMBER_PATTERN = Pattern.compile("\\d{10}");

    @Override
    public boolean isValid(ArrayList<String> phoneNumbers, ConstraintValidatorContext context) {
        boolean isValid = true;
        ArrayList<String> invalidPhoneNumbers = new ArrayList<>();

        for (int i = 0; i < phoneNumbers.size(); i++) {
            String num = phoneNumbers.get(i);
            if (!isValidPhoneNumber(num)) {
                isValid = false;
                invalidPhoneNumbers.add(String.format("Index %d: %s", i, num));
            }
        }

        if (!isValid) {
            String message = "Invalid Phone Numbers: " + String.join(", ", invalidPhoneNumbers);
            context.buildConstraintViolationWithTemplate(message)
                    .addConstraintViolation();
        }

        return isValid;
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return VALID_PHONE_NUMBER_PATTERN.matcher(phoneNumber).matches();
    }
}