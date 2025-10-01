package com.sparta.forusmarket.domain.log.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.sparta.forusmarket.domain.log.entity.OrderLog;
import com.sparta.forusmarket.domain.log.enums.LogStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class OrderLogResponse {
    private Long id;
    private Long orderId;
    private Long productId;
    private Integer quantity;
    private BigDecimal price;
    private LogStatus status;
    private String message;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createdAt;

    public static OrderLogResponse from(OrderLog log) {
        return new OrderLogResponse(
                log.getId(),
                log.getOrderId(),
                log.getProductId(),
                log.getQuantity(),
                log.getPrice(),
                log.getStatus(),
                resolveMessage(log.getStatus(), log.getMessage()),
                log.getCreatedAt()
        );
    }

    private static String resolveMessage(LogStatus status, String raw) {
        if (raw != null && !raw.isBlank()) return raw;
        if (status == null) return "";
        switch (status) {
            case SUCCESS: return "주문 완료";
            case FAILURE: return "주문 실패";
            case CANCELED: return "사용자 요청 취소";
            default: return "";
        }
    }

}
