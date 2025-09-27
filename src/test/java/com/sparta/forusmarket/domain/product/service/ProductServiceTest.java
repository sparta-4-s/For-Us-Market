package com.sparta.forusmarket.domain.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.sparta.forusmarket.domain.product.dto.request.ProductRegisterRequest;
import com.sparta.forusmarket.domain.product.dto.response.ProductResponse;
import com.sparta.forusmarket.domain.product.entity.Product;
import com.sparta.forusmarket.domain.product.repository.ProductRepository;
import com.sparta.forusmarket.utils.TestUtils;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("신규 상품 등록 성공")
    void createProduct() {
        // given
        final String productName = "프라다 리나일론 체인 스트랩 호보백";

        Product product = TestUtils.createEntity(Product.class, Map.of(
                "id", 1L,
                "name", productName
        ));
        ProductRegisterRequest request = ProductRegisterRequest.builder()
                .name(productName)
                .build();

        given(productRepository.save(any(Product.class))).willReturn(product);

        // when
        ProductResponse productResponse = productService.createProduct(request);

        // then
        assertThat(productResponse)
                .extracting("id", "name")
                .contains(1L, productName);
    }
}