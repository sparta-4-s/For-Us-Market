package com.sparta.forusmarket.domain.product.service;

import com.sparta.forusmarket.domain.hotKeywords.entity.HotKeywords;
import com.sparta.forusmarket.domain.hotKeywords.repository.HotKeywordsRepository;
import com.sparta.forusmarket.domain.product.dto.response.ProductResponse;
import com.sparta.forusmarket.domain.product.entity.Product;
import com.sparta.forusmarket.domain.product.repository.ProductRepository;
import com.sparta.forusmarket.domain.product.type.CategoryType;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final HotKeywordsRepository hotKeywordsRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    //캐싱x, DB 이용
    public Page<ProductResponse> search(String name, CategoryType category, Pageable pageable) {

        Page<Product> product = productRepository.search(name, category, pageable); //상품 검색

        hotKeywordsRepository.save(HotKeywords.of(name)); //검색 키워드 저장

        hotKeywordsRepository.findByKeyword(name).increaseCount(); //검색 키워드의 SearchCount 값 누적 증가

        return product.map(ProductResponse::of);
    }

    //캐싱o, redis 이용
    @Cacheable(value = "product", key = "'all'")
    public Page<ProductResponse> searchByCaching(String name, CategoryType category, Pageable pageable) {

        Page<Product> product = productRepository.search(name, category, pageable);

        String key = "product:ranking";
        redisTemplate.opsForZSet().incrementScore(key, name, 1);
        return product.map(ProductResponse::of);
    }
}
