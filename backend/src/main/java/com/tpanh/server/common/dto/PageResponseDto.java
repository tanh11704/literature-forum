package com.tpanh.server.common.dto;

import com.tpanh.server.common.domain.PageResult;

import java.util.List;

public record PageResponseDto<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {

    public static <T> PageResponseDto<T> fromDomain(PageResult<T> result) {
        return new PageResponseDto<>(
                result.content(),
                result.page(),
                result.size(),
                result.totalElements(),
                result.totalPages()
        );
    }
}
