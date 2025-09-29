package com.sparta.forusmarket.domain.product.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.forusmarket.common.properties.JwtSecurityProperties;
import com.sparta.forusmarket.common.security.utils.JwtUtil;
import com.sparta.forusmarket.domain.product.dto.request.ProductRegisterRequest;
import com.sparta.forusmarket.domain.product.service.ProductService;
import com.sparta.forusmarket.domain.product.type.CategoryType;
import com.sparta.forusmarket.domain.product.type.SubCategoryType;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

    @Autowired
    protected ObjectMapper objectMapper;
    @MockitoBean
    private JwtUtil jwtUtil;
    @MockitoBean
    private JwtSecurityProperties jwtSecurityProperties;
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private ProductService productService;


    @Nested
    @DisplayName("신규 상품 등록")
    class CreateProduct {
        @Test
        @DisplayName("신규 상품 등록 성공")
        void createProduct() throws Exception {
            // given
            ProductRegisterRequest request = ProductRegisterRequest.builder()
                    .name("프라다 리나일론 체인 스트랩 호보백")
                    .price(BigDecimal.valueOf(1000))
                    .stock(50)
                    .category(CategoryType.FOOD)
                    .subCategory(SubCategoryType.FRESH_FOOD)
                    .discountRate(BigDecimal.valueOf(20))
                    .build();
            // when & then
            mockMvc.perform(
                            post("/api/v1/products")
                                    .content(objectMapper.writeValueAsString(request))
                                    .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andDo(print())
                    .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("신규 상품 등록 시, 상품명이 비어있으면 등록 실패.")
        void createProduct_WithoutProductName_ShouldFail() throws Exception {
            // given
            ProductRegisterRequest request = ProductRegisterRequest.builder()
                    .price(BigDecimal.valueOf(1000))
                    .stock(50)
                    .category(CategoryType.FOOD)
                    .subCategory(SubCategoryType.FRESH_FOOD)
                    .discountRate(BigDecimal.valueOf(20))
                    .build();

            // when & then
            mockMvc.perform(
                            post("/api/v1/products")
                                    .content(objectMapper.writeValueAsString(request))
                                    .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error.status").value("400 BAD_REQUEST"))
                    .andExpect(jsonPath("$.error.code").value("BAD_REQUEST"))
                    .andExpect(jsonPath("$.error.message").value("상품명은 필수입니다."));
        }

        @Test
        @DisplayName("상품 가격이 1000원 미만이면 등록 실패")
        void shouldFail_WhenPriceIsLessThan1000() throws Exception {
            // given
            ProductRegisterRequest request = ProductRegisterRequest.builder()
                    .name("프라다 리나일론 체인 스트랩 호보백")
                    .price(BigDecimal.valueOf(999))
                    .stock(50)
                    .category(CategoryType.FOOD)
                    .subCategory(SubCategoryType.FRESH_FOOD)
                    .discountRate(BigDecimal.valueOf(20))
                    .build();

            // when & then
            mockMvc.perform(
                            post("/api/v1/products")
                                    .content(objectMapper.writeValueAsString(request))
                                    .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error.status").value("400 BAD_REQUEST"))
                    .andExpect(jsonPath("$.error.code").value("BAD_REQUEST"))
                    .andExpect(jsonPath("$.error.message").value("상품 가격은 1000원 이상이여야 합니다."));
        }

        @Test
        @DisplayName("상품 재고가 10개 미만이면 등록 실패")
        void shouldFail_WhenStockIsLessThan10() throws Exception {
            // given
            ProductRegisterRequest request = ProductRegisterRequest.builder()
                    .name("프라다 리나일론 체인 스트랩 호보백")
                    .price(BigDecimal.valueOf(1000))
                    .stock(9)
                    .category(CategoryType.FOOD)
                    .subCategory(SubCategoryType.FRESH_FOOD)
                    .discountRate(BigDecimal.valueOf(20))
                    .build();

            // when & then
            mockMvc.perform(
                            post("/api/v1/products")
                                    .content(objectMapper.writeValueAsString(request))
                                    .contentType(MediaType.APPLICATION_JSON)
                    )
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error.status").value("400 BAD_REQUEST"))
                    .andExpect(jsonPath("$.error.code").value("BAD_REQUEST"))
                    .andExpect(jsonPath("$.error.message").value("상품 재고는 10개 이상이여야 합니다."));
        }
    }

}