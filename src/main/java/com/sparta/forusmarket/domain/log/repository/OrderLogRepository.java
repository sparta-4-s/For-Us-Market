package com.sparta.forusmarket.domain.log.repository;

import com.sparta.forusmarket.domain.log.entity.OrderLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderLogRepository extends JpaRepository<OrderLog, Long> {

    Page<OrderLog> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}
