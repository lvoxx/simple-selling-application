package com.shitcode.demo1.entity;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import com.shitcode.demo1.helper.DiscountConverter;
import com.shitcode.demo1.utils.DiscountType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "promotion", indexes = @Index(name = "idx_discount_title", columnList = "title"))
public class Discount extends AbstractAuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "title", length = 60, nullable = false, unique = true)
    private String title;

    @Enumerated(EnumType.STRING)
    @Convert(converter = DiscountConverter.class)
    private List<DiscountType> types;

    @Column(name = "sales-percent-amount", precision = 0, nullable = false)
    private double salesPercentAmount;

    @Column(name = "exp-date", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime expDate;

    @OneToMany(mappedBy = "discount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products;
}