package com.sparta.forusmarket.domain.hotKeywords.repository;

import com.sparta.forusmarket.domain.hotKeywords.entity.HotKeywords;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotKeywordsRepository extends JpaRepository<HotKeywords, Long> {

    HotKeywords findByKeyword(String keyword);
}
