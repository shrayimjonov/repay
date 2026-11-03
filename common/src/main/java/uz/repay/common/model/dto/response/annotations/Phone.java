package uz.repay.common.model.dto.response.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import uz.repay.common.model.dto.response.annotations.validator.PhoneValidator;

import java.lang.annotation.*;


@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = PhoneValidator.class)
public @interface Phone {
    String message() default "{Phone.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
