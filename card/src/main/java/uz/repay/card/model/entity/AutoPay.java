package uz.repay.card.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import uz.repay.card.model.dto.svgate.request.params.SVGateAutoPayCardsParam;
import uz.repay.common.model.entity.BaseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
@Entity
@Table(name = "autopay",
        indexes = {
                @Index(name = "autopay_pinfl_ind", columnList = "person_code"),
                @Index(name = "autopay_loan_ind", columnList = "loan_ext_id"),
        }
)
@AllArgsConstructor
@NoArgsConstructor
public class AutoPay extends BaseEntity {

    @SequenceGenerator(name = "autopay_seq",
            sequenceName = "autopay_seq",
            allocationSize = 1)
    @GeneratedValue(generator = "autopay_seq",
            strategy = GenerationType.SEQUENCE)
    @Id
    private Long id;
    @Column(name = "person_code")
    private String personCode;
    @Column(name = "loan_ext_id")
    private String loanExtId;
    @Column(name = "terminal_id")
    private String terminalId;
    @Column(name = "request")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<Object, Object> request;
    @OneToMany(mappedBy = "autoPay", cascade = CascadeType.ALL)
    private List<AutoPayCard> autoPayCards = new ArrayList<>();


    public AutoPay(SVGateAutoPayCardsParam request) {
        this.personCode = request.getClientInfo().getPinfl();
        this.loanExtId = request.getClientInfo().getLoanId();
        this.terminalId = request.getClientInfo().getTerminalId();
        this.request = request.toMap();
    }
}
