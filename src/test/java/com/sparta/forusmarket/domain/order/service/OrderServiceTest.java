package com.sparta.forusmarket.domain.order.service;

import com.sparta.forusmarket.domain.order.dto.request.OrderRequest;
import com.sparta.forusmarket.domain.order.repository.OrderRepository;
import com.sparta.forusmarket.domain.product.entity.Product;
import com.sparta.forusmarket.domain.product.repository.ProductRepository;
import com.sparta.forusmarket.domain.user.entity.User;
import com.sparta.forusmarket.domain.user.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
@ActiveProfiles("test")
class OrderServiceTest {

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

    }

    @AfterEach
    void tearDown() {
        orderRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("두 개의 주문이 동시에 남은 재고를 구매하려 할 때")
    void createOrder_concurrency_test() throws BrokenBarrierException, InterruptedException {
        // given
        Product savedProduct = productRepository.save(new Product(1));
        User savedUser = userRepository.save(new User(1L));

        OrderRequest orderRequest = OrderRequest.builder()
                .userId(savedUser.getId())
                .productId(savedProduct.getId())
                .quantity(1)
                .price(BigDecimal.ONE)
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

        // then
        Product product = productRepository.findById(savedProduct.getId()).get();

        Assertions.assertEquals(0L, product.getStock());
    }
}