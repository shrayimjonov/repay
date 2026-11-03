package uz.repay.common.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Subject {
    J("juridic"),
    P("physic"),
    I("foreign banks");

    private final String description;
}
