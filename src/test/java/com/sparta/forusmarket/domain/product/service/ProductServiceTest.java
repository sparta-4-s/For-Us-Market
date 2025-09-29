package com.sparta.forusmarket.domain.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import com.sparta.forusmarket.domain.product.dto.request.ProductEditRequest;
import com.sparta.forusmarket.domain.product.dto.request.ProductRegisterRequest;
import com.sparta.forusmarket.domain.product.dto.response.ProductResponse;
import com.sparta.forusmarket.domain.product.entity.Product;
import com.sparta.forusmarket.domain.product.exception.ProductNotFoundException;
import com.sparta.forusmarket.domain.product.repository.ProductRepository;
import com.sparta.forusmarket.utils.TestUtils;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

    @Test
    @DisplayName("상품 정보 수정 성공")
    void editProduct() {
        // given
        Product product = TestUtils.createEntity(Product.class, Map.of(
                "id", 1L,
                "name", "프라다 리나일론 체인 스트랩 호보백",
                "stock", 9
        ));

        final Long productId = 1L;
        ProductEditRequest request = ProductEditRequest.builder()
                .name("수정수정")
                .stock(100)
                .build();

        given(productRepository.findById(anyLong())).willReturn(Optional.of(product));
        given(productRepository.saveAndFlush(product)).willReturn(product);

        // when
        ProductResponse productResponse = productService.editProduct(productId, request);

        // then
        assertThat(productResponse)
                .extracting("stock", "name")
                .contains(100, "수정수정");
    }

    @Nested
    @DisplayName("단일 상품 조회")
    class GetProduct {
        @Test
        @DisplayName("단일 상품 조회 성공")
        void getProductById() {
            // given
            final String productName = "프라다 리나일론 체인 스트랩 호보백";
            final Long productId = 1L;
            Product product = TestUtils.createEntity(Product.class, Map.of(
                    "id", productId,
                    "name", productName
            ));

            given(productRepository.findById(anyLong())).willReturn(Optional.of(product));

            // when
            ProductResponse productResponse = productService.getProductById(productId);

            // then
            assertThat(productResponse)
                    .extracting("id", "name")
                    .contains(1L, productName);
        }

        @Test
        @DisplayName("존재하지 않은 상품 ID 조회 시 에러 응답")
        void getProductById_WhenProductNotFound_ThrowsException() {
            // given
            final Long productId = 1L;
            given(productRepository.findById(anyLong())).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> productService.getProductById(productId))
                    .isInstanceOf(ProductNotFoundException.class)
                    .hasMessage("존재하지 않은 상품입니다.");
        }
    }
}