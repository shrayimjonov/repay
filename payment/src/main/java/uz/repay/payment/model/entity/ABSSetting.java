package uz.repay.payment.model.entity;

import jakarta.persistence.*;
import lombok.*;
import uz.repay.common.model.entity.BaseEntity;
import uz.repay.common.model.enums.Currency;
import uz.repay.payment.model.dto.core.pcMaintenance.request.PCMaintenanceAddSettingsRequestDTO;
import uz.repay.payment.model.enums.ABSDocType;
import uz.repay.payment.model.enums.ABSPayType;

@Getter
@Setter
@ToString
@Entity
@Table(name = "abs_setting",
        uniqueConstraints = {
                @UniqueConstraint(name = "onePConeOrderNum", columnNames = {"pc_id", "order_queue"})
        })
@AllArgsConstructor
@NoArgsConstructor
public class ABSSetting extends BaseEntity {
    @SequenceGenerator(name = "abs_setting_seq",
            sequenceName = "abs_setting_seq",
            allocationSize = 1)
    @GeneratedValue(generator = "abs_setting_seq",
            strategy = GenerationType.SEQUENCE)
    @Id
    private Long id;
    private String branchDebit;
    private String accountDebit;
    private String branchCredit;
    private String accountCredit;
    @Enumerated(EnumType.STRING)
    private Currency currency;
    private String purpose;
    private String purposeCode;
    @Column(name = "order_queue")
    private Integer order;
    @Enumerated(EnumType.STRING)
    private ABSDocType docType;
    @Enumerated(EnumType.STRING)
    private ABSPayType payType;
    @ManyToOne
    @ToString.Exclude
    @JoinColumn(name = "pc_id")
    private PCMaintenance pcMaintenance;

    public ABSSetting(PCMaintenanceAddSettingsRequestDTO requestDTO, PCMaintenance pcMaintenance) {
        this.branchDebit = requestDTO.getBranchDebit();
        this.accountDebit = requestDTO.getAccountDebit();
        this.branchCredit = requestDTO.getBranchCredit();
        this.accountCredit = requestDTO.getAccountCredit();
        this.currency = requestDTO.getCurrency();
        this.purpose = requestDTO.getPurpose();
        this.purposeCode = requestDTO.getPurposeCode();
        this.order = requestDTO.getOrder();
        this.docType = requestDTO.getDocType();
        this.pcMaintenance = pcMaintenance;
        this.payType = requestDTO.getPayType();
    }
}
