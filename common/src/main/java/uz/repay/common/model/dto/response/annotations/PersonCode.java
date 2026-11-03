package uz.repay.common.model.dto.response.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import uz.repay.common.model.dto.response.annotations.validator.PersonCodeValidator;

import java.lang.annotation.*;


@Target(value = {ElementType.FIELD, ElementType.PARAMETER})
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PersonCodeValidator.class)
public @interface PersonCode {

    String message() default "Person code must consist of 14 digits";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
