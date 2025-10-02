package com.sparta.forusmarket.domain.order.service;

import com.sparta.forusmarket.domain.order.dto.request.OrderRequest;
import com.sparta.forusmarket.domain.order.dto.response.OrderResponse;
import com.sparta.forusmarket.domain.order.entity.Order;
import com.sparta.forusmarket.domain.order.enums.OrderStatus;
import com.sparta.forusmarket.domain.order.exception.OrderFailedException;
import com.sparta.forusmarket.domain.order.exception.OrderNotFoundException;
import com.sparta.forusmarket.domain.order.repository.OrderRepository;
import com.sparta.forusmarket.domain.product.entity.Product;
import com.sparta.forusmarket.domain.product.exception.ProductErrorCode;
import com.sparta.forusmarket.domain.product.exception.ProductNotFoundException;
import com.sparta.forusmarket.domain.product.repository.ProductRepository;
import com.sparta.forusmarket.domain.user.entity.Address;
import com.sparta.forusmarket.domain.user.entity.User;
import com.sparta.forusmarket.domain.user.exception.InvalidUserException;
import com.sparta.forusmarket.domain.user.exception.UserErrorCode;
import com.sparta.forusmarket.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {
    final private OrderRepository orderRepository;
    final private UserRepository userRepository;
    final private ProductRepository productRepository;

    // Lettuce Lock 대상
    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest) {
        Product product = productRepository.findById(orderRequest.getProductId()).orElseThrow(
                () -> new ProductNotFoundException(ProductErrorCode.PRODUCT_NOT_FOUND)
        );
        User user = userRepository.findById(orderRequest.getUserId()).orElseThrow(
                () -> new InvalidUserException(UserErrorCode.INVALID_USER)
        );

        // 주문 저장
        Order order = Order.of(
                user,
                product,
                orderRequest.getQuantity(),
                orderRequest.getPrice(),
                OrderStatus.SUCCESS,
                new Address(
                        orderRequest.getCity(),
                        orderRequest.getStreet(),
                        orderRequest.getZipcode()
                )
        );
        Order savedOrder = orderRepository.save(order);

        // 재고 차감
        if (product.getStock() < orderRequest.getQuantity())
            throw new OrderFailedException();

        product.reduceStock(orderRequest.getQuantity());
        productRepository.flush();

        return OrderResponse.from(savedOrder);
    }

    // 배타락
    @Transactional
    public OrderResponse createOrderWithPessimistLock(OrderRequest orderRequest) {
        Product product = productRepository.findByIdWithPessimistLock(orderRequest.getProductId()).orElseThrow(
                () -> new ProductNotFoundException(ProductErrorCode.PRODUCT_NOT_FOUND)
        );

        User user = userRepository.findById(orderRequest.getUserId()).orElseThrow(
                () -> new InvalidUserException(UserErrorCode.INVALID_USER)
        );

        // 주문 저장
        Order order = Order.of(
                user,
                product,
                orderRequest.getQuantity(),
                orderRequest.getPrice(),
                OrderStatus.SUCCESS,
                new Address(
                        orderRequest.getCity(),
                        orderRequest.getStreet(),
                        orderRequest.getZipcode()
                )
        );
        Order savedOrder = orderRepository.save(order);

        // 재고 차감
        if (product.getStock() < orderRequest.getQuantity())
            throw new OrderFailedException();

        product.reduceStock(orderRequest.getQuantity());
        productRepository.flush();

        return OrderResponse.from(savedOrder);
    }

    // 낙관락
    @Transactional
    public OrderResponse createOrderWithOptimisticLock(OrderRequest orderRequest) {
        Product product = productRepository.findByIdWithOptimisticLock(orderRequest.getProductId()).orElseThrow(
                () -> new ProductNotFoundException(ProductErrorCode.PRODUCT_NOT_FOUND)
        );

        User user = userRepository.findById(orderRequest.getUserId()).orElseThrow(
                () -> new InvalidUserException(UserErrorCode.INVALID_USER)
        );

        // 주문 저장
        Order order = Order.of(
                user,
                product,
                orderRequest.getQuantity(),
                orderRequest.getPrice(),
                OrderStatus.SUCCESS,
                new Address(
                        orderRequest.getCity(),
                        orderRequest.getStreet(),
                        orderRequest.getZipcode()
                )
        );
        Order savedOrder = orderRepository.save(order);

        // 재고 차감
        if (product.getStock() < orderRequest.getQuantity())
            throw new OrderFailedException();

        product.reduceStock(orderRequest.getQuantity());
        productRepository.flush();

        return OrderResponse.from(savedOrder);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderResponse(Long orderId) {
        Order order = getOrderById(orderId);

        return OrderResponse.from(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrderPage(Pageable pageable) {
        Page<Order> orders = orderRepository.findOrderPage(pageable);

        return orders.map(OrderResponse::from);
    }

    @Transactional
    public OrderResponse cancelOrder(Long orderId) {
        Order order = getOrderById(orderId);
        order.changeStatus(OrderStatus.CANCEL);
        Order savedOrder = orderRepository.save(order);
        return OrderResponse.from(savedOrder);
    }

    @Transactional(readOnly = true)
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(
                OrderNotFoundException::new
        );
    }

    @Transactional
    public void dummyData() {
        userRepository.save(new User("1@1", "name", "pass", new Address("city", "street", "zipcode")));
    }
}
