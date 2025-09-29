package com.sparta.forusmarket.domain.product.service;

import com.sparta.forusmarket.domain.product.dto.request.ProductRegisterRequest;
import com.sparta.forusmarket.domain.product.dto.response.ProductResponse;
import com.sparta.forusmarket.domain.product.entity.Product;
import com.sparta.forusmarket.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
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
}
