package com.sparta.forusmarket.domain.hotKeywords.repository;

import com.sparta.forusmarket.domain.hotKeywords.entity.HotKeywords;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HotKeywordsRepository extends JpaRepository<HotKeywords, Long> {

    Optional<HotKeywords> findByKeyword(String keyword);
}
