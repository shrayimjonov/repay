package uz.repay.common.model.dto.response.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import uz.repay.common.model.dto.response.annotations.validator.HumoRegexValidator;


import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = HumoRegexValidator.class)
public @interface Humo {
    String message() default "{Humo.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
