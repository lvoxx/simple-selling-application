package com.shitcode.demo1.repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.shitcode.demo1.entity.ProductInteraction;

public interface ProductInteractionRepository extends JpaRepository<ProductInteraction, UUID> {

        @Query(value = """
                        SELECT pd.name, COUNT(*) AS finding_times
                        FROM (
                            SELECT pi.product_id FROM product_interaction pi
                        ) pi
                        LEFT JOIN products pd ON pi.product_id = pd.id
                        WHERE pd.name ILIKE CONCAT('%', :productName, '%')
                        GROUP BY pd.name
                        """, nativeQuery = true)
        List<Object[]> findExaggerationGroupByProductName(@Param("productName") String productName);

        List<ProductInteraction> findByUsernameContaining(String username);

        List<ProductInteraction> findByLocateAtContaining(String at);

        @Query(value = """
                        WITH temp_pi_prod AS (
                                SELECT pi.product_id, pi.locate_at, pi.on_time
                                FROM product_interaction pi
                                WHERE pi.on_time BETWEEN :startTime AND :endTime
                                ORDER BY pi.on_time
                                LIMIT COALESCE(:size, 1000000)
                                OFFSET :page * COALESCE(:size, 1000000)
                        )
                        SELECT  COALESCE(prod.prod_id, 0) AS prod_id,
                                COALESCE(prod.prod_name, 'Unknown') AS prod_name,
                                COALESCE(prod.ctg_name, 'Uncategorized') AS ctg_name,
                                COALESCE(pi.locate_at, 'Unknown') AS locate_at,
                                COALESCE(pi.on_time, NOW()) AS on_time
                        FROM temp_pi_prod pi
                        LEFT JOIN
                        (
                                SELECT  p.id AS prod_id, p.name AS prod_name,
                                        c.name AS ctg_name
                                FROM category c
                                LEFT JOIN products p
                                ON c.id = p.category_id
                        ) prod
                        ON pi.product_id = prod.prod_id
                        """, nativeQuery = true)
        List<Object[]> findPageByTimeBetween(
                        @Param("page") Integer page,
                        @Param("size") Integer size,
                        @Param("startTime") Instant startTime,
                        @Param("endTime") Instant endTime);

        // Special query to find by concatenated column within a time range
        @Query("SELECT CONCAT(p.productId, '.', p.username, '.', p.onTime, '.', p.locateAt) AS events FROM ProductInteraction p "
                        +
                        "WHERE p.onTime BETWEEN :startTime AND :endTime")
        List<String> searchFormattedColumnByTimeRange(
                        @Param("startTime") Instant startTime,
                        @Param("endTime") Instant endTime);
}
