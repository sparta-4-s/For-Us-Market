package com.sparta.forusmarket.domain.hotKeywords.entity;

import com.sparta.forusmarket.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HotKeywords extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String keyword;
    private int searchCount;

    @Builder
    public HotKeywords(String keyword) {
        this.keyword = keyword;
        this.searchCount = 1;
    }

    public static HotKeywords of(String keywords) {
        return HotKeywords.builder()
                .keyword(keywords)
                .build();
    }

    public void increaseCount() {
        this.searchCount++;
    }
}
