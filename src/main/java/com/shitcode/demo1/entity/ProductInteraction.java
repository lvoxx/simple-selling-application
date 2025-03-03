package com.shitcode.demo1.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.hibernate.validator.constraints.UUID;

import com.shitcode.demo1.utils.InteractionEvent;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "event", nullable = false, updatable = false)
    private InteractionEvent event;

    @Column(name = "product_name", updatable = false, nullable = true)
    private String productName;

    @Column(name = "category_name", updatable = false, nullable = true)
    private String categoryName;

    // Event by user
    @Column(name = "username", nullable = false, updatable = false)
    @Builder.Default
    private String by = "Anonymous User";

    // Event time
    @Column(name = "on", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Builder.Default
    private LocalDateTime on = LocalDateTime.now();

    // Time event occurs
    @Column(name = "at", nullable = false)
    @Builder.Default
    private String at = "Unknown";
}