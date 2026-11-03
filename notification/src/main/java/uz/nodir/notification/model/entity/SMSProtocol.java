package uz.nodir.notification.model.entity;

import jakarta.persistence.*;
import lombok.*;
import uz.nodir.notification.model.enums.TemplateType;
import uz.repay.common.model.entity.BaseEntity;
import uz.repay.common.model.enums.ResponseStatus;

@Getter
@Setter
@ToString
@Entity
@Table(name = "sms_protocol")
@AllArgsConstructor
@NoArgsConstructor
public class SMSProtocol extends BaseEntity {
    @SequenceGenerator(name = "sms_seq",
            sequenceName = "sms_seq",
            allocationSize = 1)
    @GeneratedValue(generator = "sms_seq",
            strategy = GenerationType.SEQUENCE)
    @Id
    private Long id;
    @Column(name = "phone", nullable = false)
    private String phone;
    @Column(name = "message", columnDefinition = "TEXT")
    private String message;
    @Column(name = "response", columnDefinition = "TEXT")
    private String response;
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private TemplateType type;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ResponseStatus status;
    @Column(name = "loan_ext_id")
    private String loanExtId;
    @Column(name = "ip_address")
    private String ipAddress;

    public SMSProtocol(String text, String phone, TemplateType type,
                       ResponseStatus responseStatus, String loanExtId,
                       String requestIPAddress) {
        this.message = text;
        this.phone = phone;
        this.type = type;
        this.status = responseStatus;
        this.loanExtId = loanExtId;
        this.ipAddress = requestIPAddress;
    }
}
