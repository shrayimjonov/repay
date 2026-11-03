package uz.repay.autodebit.service.core;

import uz.repay.autodebit.model.analytics.LoanResultAnalyticsDTO;
import uz.repay.autodebit.model.analytics.PerMinuteBreakdownDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface LoanResultAnalyticsService {
    LoanResultAnalyticsDTO getSummary(LocalDateTime from);

    LoanResultAnalyticsDTO getUniqueSummary(LocalDateTime from);

    List<PerMinuteBreakdownDTO> getPerMinuteBreakdown(LocalDateTime from);
}
