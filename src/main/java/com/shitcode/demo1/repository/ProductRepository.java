package com.shitcode.demo1.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.shitcode.demo1.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Optional<Product> findByName(@Param("name") String name);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category WHERE p.category.name = :categoryName")
    List<Product> findProductsByCategoryName(@Param("categoryName") String categoryName);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category")
    List<Product> findAllProductsWithCategory();

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category WHERE p.category.name = :categoryName")
    Page<Product> findPagedProductsByCategoryName(@Param("categoryName") String categoryName, Pageable pageable);

    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.category")
    Page<Product> findPagedAllProductsWithCategory(Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Product p SET p.discount.id = :discountId WHERE p.id = :productId")
    void updateDiscountByProductId(@Param("productId") Long productId, @Param("discountId") UUID discountId);
}
