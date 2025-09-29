package com.sparta.forusmarket.domain.product.controller;

import com.sparta.forusmarket.common.response.ApiPageResponse;
import com.sparta.forusmarket.domain.product.dto.response.ProductResponse;
import com.sparta.forusmarket.domain.product.service.ProductService;
import com.sparta.forusmarket.domain.product.type.CategoryType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ServiceController {
    private final ProductService productService;

    @GetMapping("/api/v1/products/search")
    public ResponseEntity<ApiPageResponse<ProductResponse>> search(@RequestParam(required = false) String name,
                                                                   @RequestParam(required = false) CategoryType category,
                                                                   @PageableDefault(page = 0, size = 10) Pageable pageable) {
        return ApiPageResponse.success(productService.search(name, category, pageable));
    }

    @GetMapping("/api/v2/products/search")
    public ResponseEntity<ApiPageResponse<ProductResponse>> searchByCaching(@RequestParam(required = false) String name,
                                                                            @RequestParam(required = false) CategoryType category,
                                                                            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        return ApiPageResponse.success(productService.searchByCaching(name, category, pageable));
    }
}
