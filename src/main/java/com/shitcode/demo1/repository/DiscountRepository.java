package com.shitcode.demo1.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.shitcode.demo1.entity.Discount;

public interface DiscountRepository extends JpaRepository<Discount, UUID> {

        @Query("SELECT d FROM Discount d WHERE LOWER(d.title) LIKE LOWER(CONCAT('%', :title, '%'))")
        List<Discount> findEntitiesByTitle(String title);

        @Query("SELECT d FROM Discount d WHERE d.title = :title")
        Optional<Discount> findEntityByTitle(String title);

        List<Discount> findByExpDateBetween(OffsetDateTime startDate, OffsetDateTime endDate);

        @Query("""
                            SELECT d FROM Discount d
                            WHERE (:title IS NULL OR :title = '' OR LOWER(d.title) LIKE LOWER(CONCAT('%', :title, '%')))
                            AND d.expDate BETWEEN :startDate AND :endDate
                        """)
        Page<Discount> findByTitleAndExpDateBetween(
                        @Param("title") String title,
                        @Param("startDate") OffsetDateTime startDate,
                        @Param("endDate") OffsetDateTime endDate,
                        Pageable pageable);

        // Remove relationship with products where expDate is past
        @Modifying
        @Query("UPDATE Product p SET p.discount = NULL WHERE p.discount IN " +
                        "(SELECT d FROM Discount d WHERE d.expDate < CURRENT_TIMESTAMP)")
        void removeExpiredDiscountsFromProducts();

}
