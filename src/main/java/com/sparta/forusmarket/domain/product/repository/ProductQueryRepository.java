package com.sparta.forusmarket.domain.product.repository;

import com.sparta.forusmarket.domain.product.entity.Product;
import com.sparta.forusmarket.domain.product.type.SubCategoryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductQueryRepository {

    Page<Product> findAllBySubCategory(SubCategoryType subCategoryType, Pageable pageable);

    Page<Product> findAllBySubCategoryWithCoveringIndex(SubCategoryType subCategoryType, Pageable pageable);
}
