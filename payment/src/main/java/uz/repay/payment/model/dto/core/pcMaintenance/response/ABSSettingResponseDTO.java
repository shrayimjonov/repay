package uz.repay.payment.model.dto.core.pcMaintenance.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.repay.common.model.enums.Currency;
import uz.repay.payment.model.entity.ABSSetting;
import uz.repay.payment.model.enums.ABSDocType;
import uz.repay.payment.model.enums.ABSPayType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ABSSettingResponseDTO {
    private Long id;
    private String branchDebit;
    private String accountDebit;
    private String branchCredit;
    private String accountCredit;
    private Currency currency;
    private String purpose;
    private String purposeCode;
    private Integer order;
    private ABSDocType docType;
    private ABSPayType payType;

    public ABSSettingResponseDTO(ABSSetting setting) {
        this.id = setting.getId();
        this.branchDebit = setting.getBranchCredit();
        this.accountDebit = setting.getAccountDebit();
        this.branchCredit = setting.getBranchCredit();
        this.accountCredit = setting.getAccountCredit();
        this.currency = setting.getCurrency();
        this.purpose = setting.getPurpose();
        this.purposeCode = setting.getPurposeCode();
        this.order = setting.getOrder();
        this.docType = setting.getDocType();
        this.payType = setting.getPayType();
    }
}
