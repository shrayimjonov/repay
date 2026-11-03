package uz.repay.common.model.dto.response.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import uz.repay.common.model.dto.response.annotations.validator.BranchValidator;
import uz.repay.common.model.dto.response.annotations.validator.LoanIdValidator;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = LoanIdValidator.class)
public @interface LoanId {
    String message() default "{LoanId.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
