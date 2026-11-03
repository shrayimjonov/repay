package uz.repay.card.model.entity;

import jakarta.persistence.*;
import lombok.*;
import uz.repay.card.model.dto.core.request.epos.EPosCreateRequestDTO;
import uz.repay.common.model.entity.BaseEntity;
import uz.repay.common.model.enums.Branch;
import uz.repay.common.model.enums.Processing;
import uz.repay.common.model.enums.ServiceType;


@Getter
@Setter
@ToString
@Entity
@Table(name = "epos",
        uniqueConstraints = {
                @UniqueConstraint(name = "onlyOneEPosToOneBranchAndType",
                        columnNames = {"branch", "processing", "service","exceptional"})
        }
)
@AllArgsConstructor
@NoArgsConstructor
public class EPos extends BaseEntity {
    @SequenceGenerator(name = "epos_seq",
            sequenceName = "epos_seq",
            allocationSize = 1)
    @GeneratedValue(generator = "epos_seq",
            strategy = GenerationType.SEQUENCE)
    @Id
    private Long id;
    @Column(name = "branch")
    @Enumerated(EnumType.STRING)
    private Branch branch;
    @Column(name = "processing")
    @Enumerated(EnumType.STRING)
    private Processing processing;
    @Column(name = "terminal_id")
    private String terminalId;
    @Column(name = "merchant_id")
    private String merchantId;
    @Enumerated(EnumType.STRING)
    @Column(name = "service")
    private ServiceType service;
    @Column(name = "account")
    private String account;
    @Column(name = "exceptional")
    private Boolean exceptional = false;

    public EPos(EPosCreateRequestDTO dto) {
        this.branch = dto.getBranch();
        this.processing = dto.getProcessing();
        this.terminalId = dto.getTerminalId();
        this.merchantId = dto.getMerchantId();
        this.service = dto.getService();
        this.account = dto.getAccount();
    }
}
