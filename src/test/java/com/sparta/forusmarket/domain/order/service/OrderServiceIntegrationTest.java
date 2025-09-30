package com.sparta.forusmarket.domain.order.service;

import com.sparta.forusmarket.domain.order.dto.request.OrderRequest;
import com.sparta.forusmarket.domain.order.dto.response.OrderResponse;
import com.sparta.forusmarket.domain.order.enums.OrderStatus;
import com.sparta.forusmarket.domain.order.repository.OrderRepository;
import com.sparta.forusmarket.domain.product.entity.Product;
import com.sparta.forusmarket.domain.product.repository.ProductRepository;
import com.sparta.forusmarket.domain.product.type.CategoryType;
import com.sparta.forusmarket.domain.product.type.SubCategoryType;
import com.sparta.forusmarket.domain.user.entity.Address;
import com.sparta.forusmarket.domain.user.entity.User;
import com.sparta.forusmarket.domain.user.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.concurrent.*;

@SpringBootTest
@ActiveProfiles("test")
class OrderServiceIntegrationTest {

    Long userId;
    Long productId;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        userId = !userRepository.findAll().isEmpty() ? userRepository.findAll().get(0).getId() :
                userRepository.save(new User(
                        "1@1",
                        "name",
                        "pass",
                        new Address("city",
                                "street",
                                "zipcode"
                        ))).getId();
        productId = !productRepository.findAll().isEmpty() ? productRepository.findAll().get(0).getId() :
                productRepository.save(Product.create(
                        "name",
                        BigDecimal.ONE,
                        4,
                        SubCategoryType.EBOOK,
                        CategoryType.BOOKS_MEDIA,
                        BigDecimal.ONE
                )).getId();
    }

    @AfterEach
    void tearDown() {
        orderRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("여러개의 주문이 동시에 남은 재고를 구매하려 할 때")
    void createOrder_concurrency_test() throws BrokenBarrierException, InterruptedException {
        // given
        OrderRequest orderRequest = OrderRequest.builder()
                .userId(userId)
                .productId(productId)
                .quantity(2)
                .price(BigDecimal.ONE)
                .city("city")
                .street("street")
                .zipcode("111-111")
                .build();

        int numberOfThreads = 3;
        ExecutorService executorService = Executors.newFixedThreadPool(24);
        CyclicBarrier barrier = new CyclicBarrier(numberOfThreads);

        // when
        for (int i = 0; i < numberOfThreads - 1; i++) {
            executorService.execute(() -> {
                try {
                    barrier.await();
                    orderService.createOrder(orderRequest);
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            });
        }

        barrier.await();
        executorService.shutdown();
        // redis 종료 대기
        executorService.awaitTermination(10, TimeUnit.SECONDS);

        // then
        Product product = productRepository.findById(productId).get();
        Assertions.assertEquals(0, product.getStock());
    }

    @Test
    @DisplayName("주문 생성이 성공했을 때")
    void createOrder_Success() {
        // given
        OrderRequest orderRequest = OrderRequest.builder()
                .userId(userId)
                .productId(productId)
                .quantity(1)
                .price(BigDecimal.ONE)
                .city("city")
                .street("street")
                .zipcode("zipcode")
                .build();

        // when
        OrderResponse orderResponse = orderService.createOrder(orderRequest);

        // then
        Assertions.assertEquals(OrderStatus.SUCCESS, orderResponse.status());
    }

    @Test
    @DisplayName("주문 생성이 재고 부족으로 실패했을 때")
    void createOrder_Failure() {
        // given
        OrderRequest orderRequest = OrderRequest.builder()
                .userId(userId)
                .productId(productId)
                .quantity(2)
                .price(BigDecimal.ONE)
                .city("city")
                .street("street")
                .zipcode("zipcode")
                .build();

        // when & then
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            orderService.createOrder(orderRequest);
        });
    }
}