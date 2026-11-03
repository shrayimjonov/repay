package uz.repay.common.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Branch {
    _00981("00981", "XADRA", "BANK981", 981),
    _01046("01046", "CHILONZOR", "BANK1046", 1046),
    _01069("01069", "YAKKASAROY", "BANK1069", 1069),
    _01072("01072", "MIRZO_ULUGBEK", "BANK1072", 1072),
    _01086("01086", "UCHTEPA", "BANK1086", 1086),
    _01121("01121", "OLMAZOR", "BANK1121", 1121),
    _01122("01122", "YUNUSOBOD", "BANK1122", 1122);

    private final String code;
    private final String name;
    private final String schema;
    private final int num;

}
