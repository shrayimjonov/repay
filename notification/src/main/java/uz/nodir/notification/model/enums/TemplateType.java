package uz.nodir.notification.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uz.repay.common.model.enums.UserPermission;

@Getter
@RequiredArgsConstructor
public enum TemplateType {
    CONFIRMATION(false, null, "Подтверждение карты"),
    CARD_ATTACHED(false, null, "Карта добавлена"),
    DEBT_ON(true, UserPermission.REPAY_DEBT_SMS, "Задолженность"),
    CUSTOM(false, UserPermission.REPAY_CUSTOM_SMS, "Пользовательское"),
    MIB(true, UserPermission.REPAY_MIB_SMS, "БПИ"),
    NOTICE(true, UserPermission.REPAY_NOTICE_SMS, "Предупреждающее"),
    DEBIT_PAY(false, null, "Оплата"),
    PUSH(false, null, "Уведомление"),
    MOBILE_DEEP_LINK(true, UserPermission.REPAY_MOBILE_LINK_SMS, "Ссылка на оплату через Davr Mobile"),


    ;

    private final Boolean isUsable;
    private final UserPermission role;
    private final String description;
}
