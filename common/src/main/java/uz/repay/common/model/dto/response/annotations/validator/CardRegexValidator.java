package uz.repay.common.model.dto.response.annotations.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uz.repay.common.model.dto.response.annotations.Card;

import static uz.repay.common.utils.CardUtils.*;

@RequiredArgsConstructor
@Slf4j
public class CardRegexValidator implements ConstraintValidator<Card, Object> {


    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        log.info("Starting validate card: {}", o);

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

        return o.toString()
                .matches(regexHumo())
                ||
                o.toString()
                        .matches(regexUzCard())
                ||
                o.toString()
                        .matches(regexVisaDAVR())
                ||
                o.toString().matches(regexMasterCardDAVR());
    }


}
