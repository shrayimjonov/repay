package uz.repay.common.model.dto.response.annotations.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import uz.repay.common.model.dto.response.annotations.PersonCode;


public class PersonCodeValidator implements ConstraintValidator<PersonCode, String> {

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return s != null && s.matches("\\d{14}");
    }
}
