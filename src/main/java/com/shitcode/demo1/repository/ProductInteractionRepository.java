package com.shitcode.demo1.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.shitcode.demo1.entity.ProductInteraction;
import com.shitcode.demo1.utils.InteractionEvent;

public interface ProductInteractionRepository extends JpaRepository<ProductInteraction, UUID> {

    List<ProductInteraction> findByProductName(String productName);

    List<ProductInteraction> findByEvent(InteractionEvent event);

    List<ProductInteraction> findByBy(String by);

    List<ProductInteraction> findByOn(LocalDateTime on);

    List<ProductInteraction> findByAt(String at);

    // Special query to find by concatenated column within a time range
    @Query("SELECT CONCAT(p.productName, '.', p.event, '.', p.by, '.', p.on, '.', p.at) LIKE %:searchTerm% AS events FROM ProductInteraction p " +
           "WHERE p.on BETWEEN :startTime AND :endTime")
    List<ProductInteraction> searchByFormattedColumnAndTimeRange(
        @Param("searchTerm") String searchTerm,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
}
