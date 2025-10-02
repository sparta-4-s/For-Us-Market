package com.sparta.forusmarket.domain.product.service;

import com.sparta.forusmarket.common.response.RestPage;
import com.sparta.forusmarket.domain.hotKeywords.entity.HotKeywords;
import com.sparta.forusmarket.domain.hotKeywords.repository.HotKeywordsRepository;
import com.sparta.forusmarket.domain.product.dto.request.ProductEditRequest;
import com.sparta.forusmarket.domain.product.dto.request.ProductRegisterRequest;
import com.sparta.forusmarket.domain.product.dto.response.ProductResponse;
import com.sparta.forusmarket.domain.product.entity.Product;
import com.sparta.forusmarket.domain.product.exception.ProductNotFoundException;
import com.sparta.forusmarket.domain.product.repository.ProductRepository;
import com.sparta.forusmarket.domain.product.type.SubCategoryType;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.sparta.forusmarket.domain.product.exception.ProductErrorCode.PRODUCT_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final HotKeywordsRepository hotKeywordsRepository;

    @Transactional
    public ProductResponse createProduct(ProductRegisterRequest request) {
        Product product = productRepository.save(request.toEntity());
        return ProductResponse.of(product);
    }

    public Page<ProductResponse> getProducts(SubCategoryType category, Pageable pageable) {
        return productRepository.findAllBySubCategory(category, pageable)
                .map(ProductResponse::of);
    }

    public Page<ProductResponse> getProductsWithCoveringIndex(SubCategoryType category, Pageable pageable) {
        return productRepository.findAllBySubCategoryWithCoveringIndex(category, pageable)
                .map(ProductResponse::of);
    }

    public List<ProductResponse> getProductsNoOffset(SubCategoryType category,
                                                     Long lastProductId,      // 이전 페이지의 마지막 id
                                                     LocalDateTime lastUpdatedAt, // 이전 페이지의 마지막 updatedAt
                                                     int pageSize) {
        return productRepository.findAllBySubCategoryNoOffset(category, lastProductId, lastUpdatedAt, pageSize).stream()
                .map(ProductResponse::of)
                .toList();
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

    //캐싱x, DB 이용
    @Transactional
    public Page<ProductResponse> search(String name, SubCategoryType category, Pageable pageable) {

        Page<Product> product = productRepository.search(name, category, pageable);
        if (hotKeywordsRepository.findByKeyword(name) == null) {
            hotKeywordsRepository.save(HotKeywords.of(name)); //검색 키워드 저장
        }

        hotKeywordsRepository.findByKeyword(name).increaseCount(); //검색 키워드의 SearchCount 값 누적 증가

        return product.map(ProductResponse::of);
    }

    //캐싱o, redis 이용
    @Cacheable(cacheNames = "product", key = "#name + ':' + #pageable.pageSize + '-' + #pageable.pageNumber")
    public RestPage<ProductResponse> searchByCaching(String name, SubCategoryType category, Pageable pageable) {

        Page<Product> products = productRepository.search(name, category, pageable);

        return new RestPage<>(products.map(ProductResponse::of));
    }


    @CacheEvict(value = "product")
    public void evictProductCache() { //캐시 삭제
    }
}
