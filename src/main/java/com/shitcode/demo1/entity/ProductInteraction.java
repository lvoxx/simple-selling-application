package com.shitcode.demo1.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "product_interaction")
public class ProductInteraction implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "product_id", updatable = false, nullable = true)
    private Long productId;

    // Event by user
    @Column(name = "username", nullable = false, updatable = false)
    @Builder.Default
    private String username = "Anonymous User";

    // Event time
    @Column(name = "on_time", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Builder.Default
    private Instant onTime = Instant.now();     // BRIN Index

    // Time event occurs
    @Column(name = "locate_at", nullable = false)
    @Builder.Default
    private String locateAt = "Unknown";    // B-tree Index
}