package uz.repay.common.model.dto.response.annotations.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import uz.repay.common.model.dto.response.annotations.Branch;
import uz.repay.common.utils.BranchUtils;


@Slf4j
public class BranchValidator implements ConstraintValidator<Branch, Object> {

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        return o != null && o.toString().matches(BranchUtils.branchRegexString());
    }
}