package uz.repay.common.model.dto.response.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import uz.repay.common.model.dto.response.annotations.validator.BranchValidator;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = BranchValidator.class)
public @interface Branch {
    String message() default "{Branch.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
