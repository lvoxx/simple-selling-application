package com.shitcode.demo1.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
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
@Table(name = "products", // Lowercase for PostgreSQL
        indexes = @Index(name = "idx_products_name", columnList = "name") // Lowercase index name
)
public class Product extends AbstractAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "in_stock_quantity", nullable = false)
    @Builder.Default
    private Integer inStockQuantity = 0;

    @Column(name = "in_sell_quantity", nullable = false)
    @Builder.Default
    private Integer inSellQuantity = 0;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private Double price = Double.valueOf(0.00);

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false, foreignKey = @ForeignKey(name = "fk_product_category"))
    private Category category;

    @PrePersist
    public void prePersist() {
        if (inStockQuantity == null) {
            inStockQuantity = 0;
        }
        if (inSellQuantity == null) {
            inSellQuantity = 0;
        }
        if (price == null) {
            price = Double.valueOf(0.00);
        }
    }
}
