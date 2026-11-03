package uz.davrbank.repay.loggingrest.dao;

import uz.davrbank.repay.loggingrest.dto.ActiveSessionPageResponseDTO;
import uz.davrbank.repay.loggingrest.dto.ActiveSessionsRequest;
import uz.davrbank.repay.loggingrest.dto.ActiveUserIpPageResponseDTO;
import uz.davrbank.repay.loggingrest.dto.ActiveUserPageResponseDTO;
import uz.davrbank.repay.loggingrest.dto.ActiveUsersIpRequest;
import uz.davrbank.repay.loggingrest.dto.ActiveUsersRequest;
import uz.davrbank.repay.loggingrest.dto.ApiLogFilter;
import uz.davrbank.repay.loggingrest.dto.ApiLogPageResponseDTO;
import uz.davrbank.repay.loggingrest.dto.ApiLogRow;
import uz.davrbank.repay.loggingrest.dto.LogNode;
import uz.davrbank.repay.loggingrest.dto.StatisticsResponse;

import java.time.LocalDate;
import java.util.List;

public interface ApiLogDao {

    ApiLogPageResponseDTO getRequests(ApiLogFilter filter);

    List<LogNode> getChildren(String parentSpanId);

    List<ApiLogRow> getTrace(String traceId);

    ActiveUserPageResponseDTO getActiveUsers(ActiveUsersRequest request);

    ActiveUserIpPageResponseDTO getActiveUsersByIp(ActiveUsersIpRequest request);

    ActiveSessionPageResponseDTO getActiveSessions(ActiveSessionsRequest request);

    StatisticsResponse getStatistics(LocalDate date);
}
