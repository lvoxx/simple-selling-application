package com.shitcode.demo1.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "PRODUCTS", // Uppercase table name for H2
        indexes = @Index(name = "IDX_PRODUCTS_NAME", columnList = "NAME") // Uppercase index name
)
public class Product extends AbstractAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NAME", nullable = false, length = 255)
    private String name;

    @Column(name = "IN_STOCK_QUANTITY", nullable = false, columnDefinition = "INT DEFAULT 0")
    @Builder.Default
    private Integer inStockQuantity = 0;

    @Column(name = "IN_SELL_QUANTITY", nullable = false, columnDefinition = "INT DEFAULT 0")
    @Builder.Default
    private Integer inSellQuantity = 0;

    @Column(name = "PRICE", nullable = false, columnDefinition = "DECIMAL(10, 2) DEFAULT 0.00")
    @Builder.Default
    private Double price = 0.0D;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CATEGORY_ID", nullable = false) // Foreign key column
    private Category category;
}
