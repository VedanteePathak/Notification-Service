package org.vedantee.common.Models.DTOs.Requests.Validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Constraint(validatedBy = PhoneNumberValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface PhoneNumberConstraint {
    String message() default "Each phone number must be a string of length 10 and contain only digits (0-9).";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}