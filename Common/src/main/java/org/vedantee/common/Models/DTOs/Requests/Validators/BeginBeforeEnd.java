package org.vedantee.common.Models.DTOs.Requests.Validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

//@Documented
@Constraint(validatedBy = BeginBeforeEndValidator.class)
//@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface BeginBeforeEnd {
    String message() default "Begin DateTime must be before End DateTime";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

