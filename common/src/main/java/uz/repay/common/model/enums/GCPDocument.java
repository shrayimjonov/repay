package uz.repay.common.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public enum GCPDocument {
    IDMS_RECV_IP_DOCUMENTS("Внешний паспорт"),
    IDMS_RECV_CITIZ_DOCUMENTS("Биометрический паспорт"),
    IDMS_RECV_MVD_IDCARD_CITIZEN("ID Карта"),
    IDMS_RECV_MJ_BIRTH_CERTS("");

    private final String description;
}