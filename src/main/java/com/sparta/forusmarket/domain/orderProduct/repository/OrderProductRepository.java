package com.sparta.forusmarket.domain.orderProduct.repository;

import com.sparta.forusmarket.domain.orderProduct.entity.OrderProduct;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Integer> {
}
