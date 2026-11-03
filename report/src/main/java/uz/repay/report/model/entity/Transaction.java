package uz.repay.report.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    private Long amount;
    private String phone;
    private String createdBy;
    private String fullName;
    private String processing;
    private String language;
    private String loanExtId;
    private String pan;
    private String payer;
    private LocalDateTime createdAt;
}
