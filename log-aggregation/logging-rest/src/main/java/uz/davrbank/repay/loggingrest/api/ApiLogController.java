package uz.davrbank.repay.loggingrest.api;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import uz.davrbank.repay.loggingrest.dto.*;
import uz.davrbank.repay.loggingrest.service.ApiLogService;
import uz.repay.common.model.dto.response.core.ResultData;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/api/v1/logs")
@RequiredArgsConstructor
public class ApiLogController {

    ApiLogService service;

    @PostMapping
    public ResultData<ApiLogPageResponseDTO> list(@RequestBody ApiLogFilter filter) {
        return ResultData.ok(service.list(filter));
    }

    @GetMapping("/collapse")
    public ResultData<List<LogNode>> collapse(@RequestParam String parentSpanId) {
        return ResultData.ok(service.collapse(parentSpanId));
    }

    @GetMapping("/trace/{traceId}")
    public ResultData<List<ApiLogRow>> getTrace(@PathVariable String traceId) {
        return ResultData.ok(service.trace(traceId));
    }

    @PostMapping("/active-users")
    public ResultData<ActiveUserPageResponseDTO> activeUsers(@RequestBody ActiveUsersRequest request) {
        return ResultData.ok(service.activeUsers(request));
    }

    @PostMapping("/active-users-ip")
    public ResultData<ActiveUserIpPageResponseDTO> activeUsersByIp(@RequestBody ActiveUsersIpRequest request) {
        return ResultData.ok(service.activeUsersByIp(request));
    }

    @Operation(summary = "Active sessions per username and IP")
    @PostMapping("/active-sessions")
    public ResultData<ActiveSessionPageResponseDTO> activeSessions(@RequestBody ActiveSessionsRequest request) {
        return ResultData.ok(service.activeSessions(request));
    }

    @PostMapping("/statistics")
    public ResultData<StatisticsResponse> statistics(@RequestBody StatisticsRequest request) {
        return ResultData.ok(service.statistics(request));
    }
}
