package com.sparta.forusmarket.domain.order.service;

import com.sparta.forusmarket.domain.order.dto.request.OrderRequest;
import com.sparta.forusmarket.domain.order.dto.response.OrderResponse;
import com.sparta.forusmarket.domain.order.entity.Order;
import com.sparta.forusmarket.domain.order.enums.OrderStatus;
import com.sparta.forusmarket.domain.order.repository.OrderRepository;
import com.sparta.forusmarket.domain.product.entity.Product;
import com.sparta.forusmarket.domain.product.repository.ProductRepository;
import com.sparta.forusmarket.domain.user.entity.User;
import com.sparta.forusmarket.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {
    final private OrderRepository orderRepository;
    final private UserRepository userRepository;
    final private ProductRepository productRepository;

    @Transactional
    public OrderResponse createOrder(OrderRequest orderRequest) {
        //TODO: 유저 서비스로 변경, 락
        User user = userRepository.findById(orderRequest.getUserId()).orElseThrow(
                () -> new IllegalArgumentException("User not found")
        );
        //TODO: 상품 서비스로 변경, 락
        Product product = productRepository.findById(orderRequest.getProductId()).orElseThrow(
                () -> new IllegalArgumentException("Product not found")
        );

        // 주문 저장
        Order order = Order.of(
                user,
                product,
                orderRequest.getQuantity(),
                orderRequest.getPrice(),
                OrderStatus.SUCCESS,
                orderRequest.getCity(),
                orderRequest.getStreet(),
                orderRequest.getZipcode()
        );
        Order savedOrder = orderRepository.save(order);

        try {
            // 재고 차감
            product.reduceStock(orderRequest.getQuantity());
        } catch (IllegalArgumentException e) {
            // 주문 실패 롤백
            changeFailedOrderStatus(order);

            throw new IllegalArgumentException("Order Failed");
        }

        return OrderResponse.from(savedOrder);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void changeFailedOrderStatus(Order order) {
        order.changeStatus(OrderStatus.FAIL);

        orderRepository.save(order);
        // TODO: 로깅

    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderResponse(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new IllegalArgumentException("Order not found")
        );

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
                () -> new IllegalArgumentException("Order not found")
        );
    }

    @Transactional
    public void dummyData() {
        userRepository.save(new User(1L));
    }
}
