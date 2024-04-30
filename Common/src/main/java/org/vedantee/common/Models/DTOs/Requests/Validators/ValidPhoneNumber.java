package org.vedantee.common.Models.DTOs.Requests.Validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.lang.annotation.*;

@NotBlank(message = "Phone number cannot be empty.")
@Size(min = 10, max = 10, message = "Phone number must have exactly 10 characters.")
@Pattern(regexp = "^[0-9]*$", message = "Phone number must contain only digits from 0 to 9.")
@Constraint(validatedBy = {})
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidPhoneNumber {
    String message() default "Invalid phone number";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}