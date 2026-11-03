package uz.repay.common.model.dto.response.annotations.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import uz.repay.common.model.dto.response.annotations.LoanId;


@Slf4j
public class LoanIdValidator implements ConstraintValidator<LoanId, Object> {

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        return o != null && o.toString().matches("\\d{9,}");
    }
}