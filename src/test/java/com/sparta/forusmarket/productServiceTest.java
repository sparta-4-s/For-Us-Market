package com.sparta.forusmarket;

import com.sparta.forusmarket.common._dummydata.ProductGenerator;
import com.sparta.forusmarket.domain.hotKeywords.repository.HotKeywordsRepository;
import com.sparta.forusmarket.domain.product.ProductRepository;
import com.sparta.forusmarket.domain.product.ProductService;
import com.sparta.forusmarket.domain.product.entity.Product;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;

@ExtendWith(MockitoExtension.class)
public class productServiceTest {

    @Mock
    private HotKeywordsRepository hotKeywordsRepository;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ProductGenerator productGenerator;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks // 테스트 대상
    private ProductService productService;

    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {
        hotKeywordsRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    void 캐싱_적용_전_상품조회() {
        //given
        ProductGenerator
    }

    @Test
    void 캐싱_적용_후_상품조회() {

    }
}
