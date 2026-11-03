package uz.repay.common.utils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import uz.repay.common.model.dto.request.core.params.PageParams;
import uz.repay.common.model.dto.response.core.result.PageResponse;

import java.util.List;
import java.util.function.Function;

public class PageUtils {
    public static Pageable pageableBuilder(PageParams params) {
        if (params.isDescending())
            return PageRequest.of(params.getPageNumber(), params.getPageSize(), Sort.Direction.DESC, params.getSortBy());
        else
            return PageRequest.of(params.getPageNumber(), params.getPageSize(), Sort.Direction.ASC, params.getSortBy());
    }

    public static <T, R> PageResponse<R> map(Page<T> page, Function<T, R> converter) {
        List<R> content = page.getContent()
                .stream()
                .map(converter)
                .toList();

        return new PageResponse<>(
                content,
                page.getSize(),
                page.getNumber() + 1,
                page.getNumberOfElements(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.isLast(),
                page.isFirst()
        );
    }
}
