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
import java.util.concurrent.atomic.AtomicInteger;

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
    private OrderLockService orderLockService;

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
                        90,
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
                .quantity(1)
                .price(BigDecimal.ONE)
                .city("city")
                .street("street")
                .zipcode("111-111")
                .build();

        // 배타락 테스트시 히카리 db풀 크기 만큼 설정
        int numberOfThreads = 41;
        ExecutorService executorService = Executors.newFixedThreadPool(48);
        CyclicBarrier barrier = new CyclicBarrier(numberOfThreads);

        // 시작 시간 측정
        long start = System.currentTimeMillis();
        AtomicInteger executeCount = new AtomicInteger(0);
        // when
        for (int i = 0; i < numberOfThreads - 1; i++) {
            executorService.execute(() -> {
                try {
                    barrier.await();
                    orderLockService.createOrderWithLock(orderRequest);
                    executeCount.incrementAndGet();
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            });
        }

        barrier.await();
        executorService.shutdown();

        // redis가 포함된 작업 완료 대기
        if (!executorService.awaitTermination(30, TimeUnit.SECONDS))
            executorService.shutdownNow();
        // 종료 시간 측정
        long end = System.currentTimeMillis();

        System.out.println("멀티스레드 실행 횟수 : " + executeCount.get() + "실행 시간 : " + (end - start) + "ms");

        // then
        Product product = productRepository.findById(productId).get();
        Assertions.assertEquals(50, product.getStock());
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
        OrderResponse orderResponse = orderLockService.createOrderWithLock(orderRequest);

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
            orderLockService.createOrderWithLock(orderRequest);
        });
    }
}