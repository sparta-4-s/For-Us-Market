package com.sparta.forusmarket.domain.log.service;

import com.sparta.forusmarket.common.response.ApiPageResponse;
import com.sparta.forusmarket.domain.log.dto.response.OrderLogResponse;
import com.sparta.forusmarket.domain.log.entity.OrderLog;
import com.sparta.forusmarket.domain.log.repository.OrderLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderLogService {
    private final OrderLogRepository orderLogRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveLog(OrderLog orderLog) {
        orderLogRepository.save(orderLog);
    }

    public ResponseEntity<ApiPageResponse<OrderLogResponse>> getUserLogs(Long userId, Pageable pageable){
        Page<OrderLogResponse> page = orderLogRepository
                .findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(OrderLogResponse::from);

        return ApiPageResponse.success(page);
    }
}
