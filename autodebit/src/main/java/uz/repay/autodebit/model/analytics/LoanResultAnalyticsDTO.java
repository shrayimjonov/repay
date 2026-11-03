package uz.repay.autodebit.model.analytics;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanResultAnalyticsDTO {
    private long totalProcessed;
    private double averagePerMinute;
    private LocalDateTime from;
    private LocalDateTime to;
    private long durationMinutes;
}
