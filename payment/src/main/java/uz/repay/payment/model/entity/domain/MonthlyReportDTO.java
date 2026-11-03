package uz.repay.payment.model.entity.domain;

import uz.repay.common.model.enums.AddType;

import java.time.LocalDateTime;

public interface MonthlyReportDTO {

    Long getAmount();

    LocalDateTime getDate();

    Integer getCount();

    AddType getPay_type();
}
