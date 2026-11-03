package uz.davrbank.repay.loggingrest.service;

import uz.davrbank.repay.loggingrest.dto.*;

import java.util.List;

public interface ApiLogService {

    ApiLogPageResponseDTO list(ApiLogFilter filter);

    List<LogNode> collapse(String parentSpanId);

    List<ApiLogRow> trace(String traceId);

    ActiveUserPageResponseDTO activeUsers(ActiveUsersRequest request);

    ActiveUserIpPageResponseDTO activeUsersByIp(ActiveUsersIpRequest request);

    ActiveSessionPageResponseDTO activeSessions(ActiveSessionsRequest request);

    StatisticsResponse statistics(StatisticsRequest request);

}
