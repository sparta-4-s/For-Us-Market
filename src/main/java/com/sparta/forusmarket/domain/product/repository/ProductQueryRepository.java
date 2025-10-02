package com.sparta.forusmarket.domain.product.repository;

import com.sparta.forusmarket.domain.product.entity.Product;
import com.sparta.forusmarket.domain.product.type.SubCategoryType;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductQueryRepository {

    Page<Product> findAllBySubCategory(SubCategoryType subCategoryType, Pageable pageable);

    Page<Product> findAllBySubCategoryWithCoveringIndex(SubCategoryType subCategoryType, Pageable pageable);

    List<Product> findAllBySubCategoryNoOffset(
            SubCategoryType subCategoryType,
            Long lastProductId,      // 이전 페이지의 마지막 id
            LocalDateTime lastUpdatedAt, // 이전 페이지의 마지막 updatedAt
            int pageSize
    );

}
