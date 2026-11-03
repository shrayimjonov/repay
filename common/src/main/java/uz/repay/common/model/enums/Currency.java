package uz.repay.common.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import uz.repay.common.model.dto.response.core.SimpleResponse;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum Currency {
    UZS(860),
    USD(840),
    EUR(978),
    RUB(643);

    private final Integer code;

    public static Currency getCurrencyByCode(Integer code) {
        return Arrays.stream(Currency.values())
                .filter(e -> e.getCode().equals(code))
                .findFirst()
                .orElse(UZS);

    }
}
