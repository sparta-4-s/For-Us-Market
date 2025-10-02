package com.sparta.forusmarket.domain.product.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.forusmarket.domain.product.entity.Product;
import com.sparta.forusmarket.domain.product.entity.QProduct;
import com.sparta.forusmarket.domain.product.type.SubCategoryType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.ObjectUtils;

@Slf4j
@RequiredArgsConstructor
public class ProductQueryRepositoryImpl implements ProductQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Product> findAllBySubCategory(SubCategoryType subCategoryType, Pageable pageable) {

        QProduct product = QProduct.product;
        List<Product> products = jpaQueryFactory
                .selectFrom(product)
                .where(eqSubCategory(product, subCategoryType))
                .orderBy(product.updatedAt.desc(), product.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(Wildcard.count)
                .from(product)
                .where(eqSubCategory(product, subCategoryType));

        return PageableExecutionUtils.getPage(products, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<Product> findAllBySubCategoryWithCoveringIndex(SubCategoryType subCategoryType, Pageable pageable) {
        QProduct product = QProduct.product;

        List<Long> ids = jpaQueryFactory
                .select(product.id)
                .from(product)
                .where(eqSubCategory(product, subCategoryType))
                .orderBy(product.updatedAt.desc(), product.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<Product> products = jpaQueryFactory
                .selectFrom(product)
                .where(product.id.in(ids))
                .orderBy(product.updatedAt.desc(), product.id.desc())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(Wildcard.count)
                .from(product)
                .where(eqSubCategory(product, subCategoryType));

        return PageableExecutionUtils.getPage(products, pageable, countQuery::fetchOne);
    }

    @Override
    public List<Product> findAllBySubCategoryNoOffset(
            SubCategoryType subCategoryType,
            Long lastProductId,      // 이전 페이지의 마지막 id
            LocalDateTime lastUpdatedAt, // 이전 페이지의 마지막 updatedAt
            int pageSize
    ) {
        QProduct product = QProduct.product;

        BooleanBuilder whereBuilder = new BooleanBuilder();
        whereBuilder.and(eqSubCategory(product, subCategoryType));

        // No-Offset 조건 (커서 기반)
        if (lastUpdatedAt != null && lastProductId != null) {
            whereBuilder.and(
                    product.updatedAt.lt(lastUpdatedAt) // updated_at<?
                            .or(product.updatedAt.eq(lastUpdatedAt) // updated_at=?
                                    .and(product.id.lt(lastProductId))) // id<?
            );
        }

        return jpaQueryFactory
                .selectFrom(product)
                .where(whereBuilder)
                .orderBy(product.updatedAt.desc(), product.id.desc())
                .limit(pageSize)
                .fetch();
    }

    private BooleanExpression eqSubCategory(QProduct product, SubCategoryType subCategoryType) {
        if (ObjectUtils.isEmpty(subCategoryType)) {
            return null;
        }

        return product.subCategory.eq(subCategoryType);
    }
}
