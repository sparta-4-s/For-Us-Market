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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootTest
@ActiveProfiles("test")
class OrderServiceIntegrationTest {

    Long userId;
    Long productId;
    List<String> logs = new ArrayList<>();
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderService orderService;
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
        createDummyProduct(10);
    }

    @AfterEach
    void tearDown() {
        orderRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();
    }

    void createDummyProduct(int stock) {
        productId = productRepository.save(Product.create(
                "name",
                BigDecimal.ONE,
                stock,
                SubCategoryType.EBOOK,
                CategoryType.BOOKS_MEDIA,
                BigDecimal.ONE
        )).getId();
    }

    void createOrder_LettuceLock(int numberOfThreads) throws BrokenBarrierException, InterruptedException {
        // given
        createDummyProduct(100);
        OrderRequest orderRequest = OrderRequest.builder()
                .userId(userId)
                .productId(productId)
                .quantity(1)
                .price(BigDecimal.ONE)
                .city("city")
                .street("street")
                .zipcode("111-111")
                .build();

        // 시작 시간 측정
        ExecutorService executorService = Executors.newFixedThreadPool(60);
        CyclicBarrier barrier = new CyclicBarrier(numberOfThreads + 1);

        long start = System.currentTimeMillis();
        AtomicInteger executeCount = new AtomicInteger(0);
        // when
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.execute(() -> {
                try {
                    barrier.await();
                    orderLockService.createOrderWithLettuceLock(orderRequest);
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

        logs.add("Lettuce Lock 멀티스레드 실행 횟수 : " + executeCount.get() + " 실행 시간 : " + (end - start) + "ms");
    }

    void createOrder_RedissonLock(int numberOfThreads) throws BrokenBarrierException, InterruptedException {
        // given
        createDummyProduct(100);
        OrderRequest orderRequest = OrderRequest.builder()
                .userId(userId)
                .productId(productId)
                .quantity(1)
                .price(BigDecimal.ONE)
                .city("city")
                .street("street")
                .zipcode("111-111")
                .build();

        // 시작 시간 측정
        ExecutorService executorService = Executors.newFixedThreadPool(60);
        CyclicBarrier barrier = new CyclicBarrier(numberOfThreads + 1);

        long start = System.currentTimeMillis();
        AtomicInteger executeCount = new AtomicInteger(0);
        // when
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.execute(() -> {
                try {
                    barrier.await();
                    orderLockService.createOrderWithRedissonLock(orderRequest);
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

        logs.add("Redisson Lock 멀티스레드 실행 횟수 : " + executeCount.get() + " 실행 시간 : " + (end - start) + "ms");
    }

    void createOrder_PessimistLock(int numberOfThreads) throws BrokenBarrierException, InterruptedException {
        // given
        createDummyProduct(100);
        OrderRequest orderRequest = OrderRequest.builder()
                .userId(userId)
                .productId(productId)
                .quantity(1)
                .price(BigDecimal.ONE)
                .city("city")
                .street("street")
                .zipcode("111-111")
                .build();

        // 시작 시간 측정
        ExecutorService executorService = Executors.newFixedThreadPool(60);
        CyclicBarrier barrier = new CyclicBarrier(numberOfThreads + 1);

        long start = System.currentTimeMillis();
        AtomicInteger executeCount = new AtomicInteger(0);
        // when
        for (int i = 0; i < numberOfThreads; i++) {
            executorService.execute(() -> {
                try {
                    barrier.await();
                    orderService.createOrderWithPessimistLock(orderRequest);
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

        logs.add("Pessimist Lock 멀티스레드 실행 횟수 : " + executeCount.get() + " 실행 시간 : " + (end - start) + "ms");
    }

    @Test
    @DisplayName("여러개의 주문이 동시에 남은 재고를 구매하려 할 때")
    void createOrder_concurrency_test() throws BrokenBarrierException, InterruptedException {
        int nOT = 10;
        createOrder_RedissonLock(nOT);
        createOrder_LettuceLock(nOT);
        createOrder_PessimistLock(nOT);
        createOrder_RedissonLock(nOT);
        createOrder_LettuceLock(nOT);
        createOrder_PessimistLock(nOT);

        for (String log : logs)
            System.out.println(log);

        // then
        Product product = productRepository.findById(productId).get();
        Assertions.assertEquals(2, product.getStock());
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
        OrderResponse orderResponse = orderLockService.createOrderWithLettuceLock(orderRequest);

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
            orderLockService.createOrderWithLettuceLock(orderRequest);
        });
    }
}