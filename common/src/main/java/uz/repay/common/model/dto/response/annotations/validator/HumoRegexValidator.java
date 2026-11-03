package uz.repay.common.model.dto.response.annotations.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uz.repay.common.model.dto.response.annotations.Humo;
import uz.repay.common.utils.CardUtils;

import static uz.repay.common.utils.CardUtils.isValidCreditCardNumber;

@RequiredArgsConstructor
@Slf4j
public class HumoRegexValidator implements ConstraintValidator<Humo, Object> {


    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        log.info("Starting validate HUMO card: {}", o);


        if (o == null) {
            constraintValidatorContext
                    .buildConstraintViolationWithTemplate("Card is null")
                    .addConstraintViolation();
            return false;
        }


        if (!isValidCreditCardNumber(o.toString())) {
            constraintValidatorContext
                    .buildConstraintViolationWithTemplate("This card type incorrect. Checked by Luhn algorithm")
                    .addConstraintViolation();
            return false;
        }


        return o.toString().matches(CardUtils.regexHumo());
    }


}
