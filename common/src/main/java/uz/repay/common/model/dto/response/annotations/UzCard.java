package uz.repay.common.model.dto.response.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import uz.repay.common.model.dto.response.annotations.validator.UzCardRegexValidator;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = UzCardRegexValidator.class)
public @interface UzCard {
    String message() default "{UzCard.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
