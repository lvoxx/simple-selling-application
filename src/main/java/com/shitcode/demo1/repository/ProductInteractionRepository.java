package com.shitcode.demo1.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.shitcode.demo1.entity.ProductInteraction;

public interface ProductInteractionRepository extends JpaRepository<ProductInteraction, UUID> {

        List<ProductInteraction> findByProductName(String productName);

        List<ProductInteraction> findByUsername(String username);

        List<ProductInteraction> findByLocateAt(String at);

        @Query(value = """
                        WITH temp_pi_prod AS (
                                SELECT pi.product_id, pi.locate_at, pi.on_time FROM product_interaction p
                                WHERE pi.on_time BETWEEN :startTime AND :endTime
                                ORDER BY pi.on_time
                                LIMIT COALESCE(:size, 1000000)
                                OFFSET :page * COALESCE(:size, 1000000)
                        )
                        SELECT p.id AS prod_id, p.name AS prod_name, c.name AS ctg_name, pi.locate_at, pi.on_time
                        FROM temp_pi_prod pi
                        LEFT JOIN
                        (
                                SELECT p.id AS prod_id, p.name AS prod_name, c.name AS ctg_name
                                FROM category c
                                LEFT JOIN products p
                                ON c.id = p.category_id
                                WHERE p.category_id IS NOT NULL
                        ) p
                        ON pi.product_id = p.prod_id
                        WHERE pi.product_id IS NOT NULL
                        """, nativeQuery = true)
        List<Object[]> findPageByTime(
                        @Param("page") Integer page,
                        @Param("size") Integer size,
                        @Param("startTime") LocalDateTime startTime,
                        @Param("endTime") LocalDateTime endTime);

        // Special query to find by concatenated column within a time range
        @Query("SELECT CONCAT(p.productName, '.', p.eventStage, '.', p.userStage, '.', p.onTime, '.', p.locateAt) AS events FROM ProductInteraction p "
                        +
                        "WHERE p.onTime BETWEEN :startTime AND :endTime")
        List<String> searchFormattedColumnByTimeRange(
                        @Param("startTime") LocalDateTime startTime,
                        @Param("endTime") LocalDateTime endTime);
}
