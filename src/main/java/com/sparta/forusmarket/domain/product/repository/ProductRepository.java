package com.sparta.forusmarket.domain.product.repository;

import com.sparta.forusmarket.domain.product.entity.Product;
import com.sparta.forusmarket.domain.product.type.CategoryType;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("""
             SELECT p
             FROM Product p
             WHERE (:keyword IS NULL OR p.name LIKE %:keyword%)
                AND (:category IS NULL OR p.category = :category)
            """)
    Page<Product> search(@Param("keyword") String keyword,
                         @Param("category") CategoryType category,
                         Pageable pageable);
}
