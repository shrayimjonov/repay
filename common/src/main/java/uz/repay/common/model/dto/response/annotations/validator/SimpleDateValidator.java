package uz.repay.common.model.dto.response.annotations.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import uz.repay.common.model.dto.response.annotations.SimpleDate;
import uz.repay.common.utils.DateUtils;


@Slf4j
public class SimpleDateValidator implements ConstraintValidator<SimpleDate, Object> {

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        return o != null && o.toString().matches(DateUtils.ddMMyyyyRegex());
    }
}