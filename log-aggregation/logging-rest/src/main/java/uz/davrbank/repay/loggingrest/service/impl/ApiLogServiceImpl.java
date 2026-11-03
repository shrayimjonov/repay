package uz.davrbank.repay.loggingrest.service.impl;

import org.springframework.stereotype.Service;
import uz.davrbank.repay.loggingrest.dao.ApiLogDao;
import uz.davrbank.repay.loggingrest.dto.*;
import uz.davrbank.repay.loggingrest.service.ApiLogService;
import uz.repay.common.model.dto.request.core.params.PageParams;

import java.util.List;

@Service
public class ApiLogServiceImpl implements ApiLogService {

    private static final int DEFAULT_SINCE_MINUTES = 60;
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 500;

    private final ApiLogDao dao;

    public ApiLogServiceImpl(ApiLogDao dao) {
        this.dao = dao;
    }

    @Override
    public ApiLogPageResponseDTO list(ApiLogFilter filter) {
        if (filter.getSinceMinutes() == null) {
            filter.setSinceMinutes(DEFAULT_SINCE_MINUTES);
        }
        normalizePaging(filter);
        return dao.getRequests(filter);
    }

    @Override
    public List<LogNode> collapse(String parentSpanId) {
        return dao.getChildren(parentSpanId);
    }

    @Override
    public List<ApiLogRow> trace(String traceId) {
        return dao.getTrace(traceId);
    }

    @Override
    public ActiveUserPageResponseDTO activeUsers(ActiveUsersRequest request) {
        normalizePaging(request);
        return dao.getActiveUsers(request);
    }

    @Override
    public ActiveUserIpPageResponseDTO activeUsersByIp(ActiveUsersIpRequest request) {
        normalizePaging(request);
        return dao.getActiveUsersByIp(request);
    }

    @Override
    public ActiveSessionPageResponseDTO activeSessions(ActiveSessionsRequest request) {
        normalizePaging(request);
        return dao.getActiveSessions(request);
    }

    @Override
    public StatisticsResponse statistics(StatisticsRequest request) {
        return dao.getStatistics(request.date());
    }

    private void normalizePaging(PageParams params) {
        if (params.getPageNumber() < 0) {
            params.setPageNumber(0);
        }
        if (params.getPageSize() <= 0) {
            params.setPageSize(DEFAULT_PAGE_SIZE);
        }
        if (params.getPageSize() > MAX_PAGE_SIZE) {
            params.setPageSize(MAX_PAGE_SIZE);
        }
    }
}
