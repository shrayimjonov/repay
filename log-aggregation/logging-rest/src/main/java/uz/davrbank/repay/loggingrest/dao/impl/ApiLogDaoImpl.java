package uz.davrbank.repay.loggingrest.dao.impl;

import org.springframework.stereotype.Repository;
import uz.davrbank.repay.loggingrest.dao.ApiLogDao;
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
import uz.davrbank.repay.loggingrest.repository.ApiLogRepository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class ApiLogDaoImpl implements ApiLogDao {

    private final ApiLogRepository repository;

    public ApiLogDaoImpl(ApiLogRepository repository) {
        this.repository = repository;
    }

    @Override
    public ApiLogPageResponseDTO getRequests(ApiLogFilter filter) {
        return repository.roots(filter);
    }

    @Override
    public List<LogNode> getChildren(String parentSpanId) {
        return repository.children(parentSpanId);
    }

    @Override
    public List<ApiLogRow> getTrace(String traceId) {
        return repository.traceTree(traceId);
    }

    @Override
    public ActiveUserPageResponseDTO getActiveUsers(ActiveUsersRequest request) {
        return repository.activeUsers(request);
    }

    @Override
    public ActiveUserIpPageResponseDTO getActiveUsersByIp(ActiveUsersIpRequest request) {
        return repository.activeUsersByIp(request);
    }

    @Override
    public ActiveSessionPageResponseDTO getActiveSessions(ActiveSessionsRequest request) {
        return repository.activeSessions(request);
    }

    @Override
    public StatisticsResponse getStatistics(LocalDate date) {
        return repository.statistics(date);
    }
}
