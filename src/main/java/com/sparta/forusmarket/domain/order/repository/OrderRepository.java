package com.sparta.forusmarket.domain.order.repository;

import com.sparta.forusmarket.domain.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o JOIN FETCH User u on u = o.user JOIN FETCH Product p on p = o.product")
    Page<Order> findOrderPage(Pageable pageable);

    @EntityGraph(attributePaths = {"user", "product"})
    Optional<Order> findTopByUserIdAndProductIdOrderByCreatedAtDesc(Long user_id, Long product_id);
}
