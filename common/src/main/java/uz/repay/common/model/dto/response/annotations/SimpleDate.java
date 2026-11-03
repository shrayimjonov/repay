package uz.repay.common.model.dto.response.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import uz.repay.common.model.dto.response.annotations.validator.BranchValidator;
import uz.repay.common.model.dto.response.annotations.validator.SimpleDateValidator;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = SimpleDateValidator.class)
public @interface SimpleDate {
    String message() default "{SimpleDate.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
