package uz.repay.common.model.dto.response.annotations.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import uz.repay.common.model.dto.response.annotations.Phone;
import uz.repay.common.utils.CommonUtils;


@Slf4j
public class PhoneValidator implements ConstraintValidator<Phone, Object> {

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        return o != null && o.toString().matches(CommonUtils.phoneRegex());
    }
}