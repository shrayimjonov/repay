package uz.repay.common.model.dto.response.core.result;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;


public record PageResponse<T>(
        List<T> data,
        int size,
        int currentPage,
        int numberOfElements,
        int totalPages,
        long totalElements,
        boolean isLast,
        boolean isFirst) {
}
