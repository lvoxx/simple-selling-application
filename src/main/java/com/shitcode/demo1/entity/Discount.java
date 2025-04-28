package com.shitcode.demo1.entity;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import com.shitcode.demo1.utils.DiscountType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "discounts", indexes = @Index(name = "idx_discount_title", columnList = "title"))
public class Discount extends AbstractAuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "title", length = 60, nullable = false, unique = true)
    private String title; // B-Tree Index

    @Enumerated(EnumType.STRING)
    private DiscountType type;

    @Column(name = "sales_percent_amount", precision = 0, nullable = false)
    private Double salesPercentAmount;

    @Column(name = "exp_date", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime expDate; // BRIN Index

    @OneToMany(mappedBy = "discount", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products;

}