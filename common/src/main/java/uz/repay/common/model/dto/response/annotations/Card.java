package uz.repay.common.model.dto.response.annotations;

import jakarta.validation.Payload;
import uz.repay.common.model.dto.response.annotations.validator.CardRegexValidator;


import jakarta.validation.Constraint;
import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = CardRegexValidator.class)
public @interface Card {
    String message() default "{Card.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
