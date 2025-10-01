package com.sparta.forusmarket.domain.log.service;

import com.sparta.forusmarket.common.response.ApiPageResponse;
import com.sparta.forusmarket.common.response.RestPage;
import com.sparta.forusmarket.domain.log.dto.response.OrderLogResponse;
import com.sparta.forusmarket.domain.log.entity.OrderLog;
import com.sparta.forusmarket.domain.log.repository.OrderLogRepository;
import com.sparta.forusmarket.domain.product.dto.response.ProductResponse;
import com.sparta.forusmarket.domain.product.entity.Product;
import com.sparta.forusmarket.domain.product.type.SubCategoryType;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderLogService {
    private final OrderLogRepository orderLogRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    // 로그 저장
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @CacheEvict(cacheNames = "order:log", allEntries = true, beforeInvocation = true)
    public void saveLog(OrderLog orderLog) {
        orderLogRepository.save(orderLog);
    }

    // 캐싱 X
    public ResponseEntity<ApiPageResponse<OrderLogResponse>> getUserLogs(Long userId, Pageable pageable){
        Page<OrderLogResponse> page = orderLogRepository
                .findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(OrderLogResponse::from);
        return ApiPageResponse.success(page);
    }

    // 캐싱 O
    @Cacheable(
            cacheNames = "order:log",
            key = "#userId + ':' + #pageable.pageNumber + ':' + #pageable.pageSize + ':' + #pageable.sort"
    )
    public RestPage<OrderLogResponse> getCachedUserLogs(Long userId, Pageable pageable){
        Page<OrderLog> log = orderLogRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return new RestPage<>(log.map(OrderLogResponse::from));
    }
}
