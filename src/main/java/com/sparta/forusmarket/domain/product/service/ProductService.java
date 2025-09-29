package com.sparta.forusmarket.domain.product.service;

import static com.sparta.forusmarket.domain.product.exception.ProductErrorCode.PRODUCT_NOT_FOUND;

import com.sparta.forusmarket.domain.product.dto.request.ProductEditRequest;
import com.sparta.forusmarket.domain.product.dto.request.ProductRegisterRequest;
import com.sparta.forusmarket.domain.product.dto.response.ProductResponse;
import com.sparta.forusmarket.domain.product.entity.Product;
import com.sparta.forusmarket.domain.product.exception.ProductNotFoundException;
import com.sparta.forusmarket.domain.product.repository.ProductRepository;
import com.sparta.forusmarket.domain.product.type.SubCategoryType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public ProductResponse createProduct(ProductRegisterRequest request) {
        Product product = productRepository.save(request.toEntity());
        return ProductResponse.of(product);
    }

    public Page<ProductResponse> getProducts(SubCategoryType category, Pageable pageable) {
        return productRepository.findAllBySubCategory(category, pageable)
                .map(ProductResponse::of);
    }

    public ProductResponse getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(PRODUCT_NOT_FOUND));
        return ProductResponse.of(product);
    }

    @Transactional
    public ProductResponse editProduct(Long productId, ProductEditRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(PRODUCT_NOT_FOUND));

        product.updateAll(
                request.getName(),
                request.getPrice(),
                request.getStock(),
                request.getDiscountRate(),
                request.getCategory(),
                request.getSubCategory()
        );

        return ProductResponse.of(productRepository.saveAndFlush(product));
    }
}
