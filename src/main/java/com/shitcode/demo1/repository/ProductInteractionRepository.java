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

    List<ProductInteraction> findByEventStage(InteractionEvent eventStage);

    List<ProductInteraction> findByUserStage(String userStage);

    List<ProductInteraction> findByOnTime(LocalDateTime on);

    List<ProductInteraction> findByLocateAt(String at);

    // Special query to find by concatenated column within a time range
    @Query("SELECT CONCAT(p.productName, '.', p.eventStage, '.', p.userStage, '.', p.onTime, '.', p.locateAt) AS events FROM ProductInteraction p " +
           "WHERE p.eventStage = :eventStage AND p.onTime BETWEEN :startTime AND :endTime")
    List<String> searchByFormattedColumnAndTimeRange(
        @Param("eventStage") InteractionEvent eventStage,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
}
