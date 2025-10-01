package com.sparta.forusmarket.domain.product.controller;

import com.sparta.forusmarket.common.response.ApiPageResponse;
import com.sparta.forusmarket.common.response.ApiResponse;
import com.sparta.forusmarket.domain.product.dto.request.ProductEditRequest;
import com.sparta.forusmarket.domain.product.dto.request.ProductRegisterRequest;
import com.sparta.forusmarket.domain.product.dto.response.ProductResponse;
import com.sparta.forusmarket.domain.product.service.ProductService;
import com.sparta.forusmarket.domain.product.type.SubCategoryType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @RequestBody ProductRegisterRequest request) {
        ProductResponse productResponse = productService.createProduct(request);
        return ApiResponse.created(productResponse);
    }

    @GetMapping
    public ResponseEntity<ApiPageResponse<ProductResponse>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) SubCategoryType category) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductResponse> productResponses = productService.getProducts(category, pageable);
        return ApiPageResponse.success(productResponses);
    }

    @GetMapping("/api/v2/products")
    public ResponseEntity<ApiPageResponse<ProductResponse>> getProductsWithCoveringIndex(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) SubCategoryType category) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductResponse> productResponses = productService.getProductsWithCoveringIndex(category, pageable);
        return ApiPageResponse.success(productResponses);
    }

    @GetMapping("/api/v3/products")
    public ResponseEntity<List<ProductResponse>> getProductsNoOffset(
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long lastProductId,
            @RequestParam(required = false) LocalDateTime lastUpdatedAt,
            @RequestParam(required = false) SubCategoryType category) {

        List<ProductResponse> productResponses = productService.getProductsNoOffset(category, lastProductId,
                lastUpdatedAt, size);
        return ResponseEntity.ok(productResponses);
    }

    @GetMapping("/api/v1/products/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProduct(@PathVariable Long productId) {
        ProductResponse productResponse = productService.getProductById(productId);
        return ApiResponse.success(productResponse);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductResponse>> editProduct(@Valid @RequestBody ProductEditRequest request,
                                                                    @PathVariable Long productId) {
        ProductResponse productResponse = productService.editProduct(productId, request);
        return ApiResponse.success(productResponse);
    }

    @GetMapping("/api/v1/products/search")
    public ResponseEntity<ApiPageResponse<ProductResponse>> search(@RequestParam(required = false) String name,
                                                                   @RequestParam(required = false) SubCategoryType category,
                                                                   @PageableDefault(page = 0, size = 10) Pageable pageable) {
        return ApiPageResponse.success(productService.search(name, category, pageable));
    }

    @GetMapping("/api/v2/products/search")
    public ResponseEntity<ApiPageResponse<ProductResponse>> searchByCaching(@RequestParam(required = false) String name,
                                                                            @RequestParam(required = false) SubCategoryType category,
                                                                            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        return ApiPageResponse.success(productService.searchByCaching(name, category, pageable));
    }
}
