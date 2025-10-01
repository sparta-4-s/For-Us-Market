package com.sparta.forusmarket.domain.product.dto.response;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public record ProductPageResponse<T>(
        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean last
) implements Serializable {

    public static <T> ProductPageResponse<T> from(Page<T> p) {
        List<T> copy = new ArrayList<>(p.getContent());
        return new ProductPageResponse<>(
                copy, p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages(), p.isLast()
        );
    }
}