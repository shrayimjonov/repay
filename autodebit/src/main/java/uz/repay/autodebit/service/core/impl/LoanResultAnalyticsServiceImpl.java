package uz.repay.autodebit.service.core.impl;

import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.DateOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import uz.repay.autodebit.model.analytics.LoanResultAnalyticsDTO;
import uz.repay.autodebit.model.analytics.PerMinuteBreakdownDTO;
import uz.repay.autodebit.mongo.entity.LoanResult;
import uz.repay.autodebit.service.core.LoanResultAnalyticsService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanResultAnalyticsServiceImpl implements LoanResultAnalyticsService {
    private static final long ANALYTICS_WINDOW_MINUTES = 10;
    private static final String CREATED_TIME = "createdTime";
    private static final String LOAN_EXT_ID = "loanExtId";
    private static final String MINUTE = "minute";
    private static final String COUNT = "count";
    private static final String TOTAL = "total";
    private static final DateOperators.Timezone TASHKENT_TIMEZONE = DateOperators.Timezone.valueOf("+05:00");

    private final MongoTemplate mongoTemplate;

    @Override
    public LoanResultAnalyticsDTO getSummary(LocalDateTime from) {
        TimeRange range = buildTimeRange(from);

        long totalProcessed = mongoTemplate.count(
                Query.query(createdTimeCriteria(range.start(), range.end())),
                LoanResult.class
        );
        long durationMinutes = Math.max(ChronoUnit.MINUTES.between(range.start(), range.end()), 1);
        double averagePerMinute = roundToTwoDecimals(totalProcessed / (double) durationMinutes);

        return LoanResultAnalyticsDTO.builder()
                .totalProcessed(totalProcessed)
                .averagePerMinute(averagePerMinute)
                .from(range.start())
                .to(range.end())
                .durationMinutes(durationMinutes)
                .build();
    }

    @Override
    public LoanResultAnalyticsDTO getUniqueSummary(LocalDateTime from) {
        TimeRange range = buildTimeRange(from);

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(createdTimeCriteria(range.start(), range.end())),
                Aggregation.group(LOAN_EXT_ID),
                Aggregation.count().as(TOTAL)
        );

        long totalProcessed = mongoTemplate.aggregate(aggregation, LoanResult.class, Document.class)
                .getMappedResults()
                .stream()
                .findFirst()
                .map(document -> getLong(document, TOTAL))
                .orElse(0L);

        long durationMinutes = Math.max(ChronoUnit.MINUTES.between(range.start(), range.end()), 1);
        double averagePerMinute = roundToTwoDecimals(totalProcessed / (double) durationMinutes);

        return LoanResultAnalyticsDTO.builder()
                .totalProcessed(totalProcessed)
                .averagePerMinute(averagePerMinute)
                .from(range.start())
                .to(range.end())
                .durationMinutes(durationMinutes)
                .build();
    }

    @Override
    public List<PerMinuteBreakdownDTO> getPerMinuteBreakdown(LocalDateTime from) {
        TimeRange range = buildTimeRange(from);

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(createdTimeCriteria(range.start(), range.end())),
                Aggregation.project(CREATED_TIME)
                        .and(DateOperators.DateToString
                                .dateOf(CREATED_TIME)
                                .toString("%Y-%m-%dT%H:%M")
                                .withTimezone(TASHKENT_TIMEZONE))
                        .as(MINUTE),
                Aggregation.group(MINUTE).count().as(COUNT),
                Aggregation.sort(Sort.by(Sort.Direction.ASC, "_id"))
        );

        return mongoTemplate.aggregate(aggregation, LoanResult.class, Document.class)
                .getMappedResults()
                .stream()
                .map(this::toPerMinuteBucket)
                .toList();
    }

    private PerMinuteBreakdownDTO toPerMinuteBucket(Document document) {
        return PerMinuteBreakdownDTO.builder()
                .minute(document.getString("_id"))
                .count(getLong(document, COUNT))
                .build();
    }

    private Criteria createdTimeCriteria(LocalDateTime from, LocalDateTime to) {
        return Criteria.where(CREATED_TIME).gte(from).lte(to);
    }

    private TimeRange buildTimeRange(LocalDateTime from) {
        LocalDateTime end = from == null ? LocalDateTime.now() : from;
        return new TimeRange(end.minusMinutes(ANALYTICS_WINDOW_MINUTES), end);
    }

    private double roundToTwoDecimals(double value) {
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private long getLong(Document document, String key) {
        Object value = document.get(key);
        if (value instanceof Number number) {
            return number.longValue();
        }
        return 0L;
    }

    private record TimeRange(LocalDateTime start, LocalDateTime end) {
    }
}
