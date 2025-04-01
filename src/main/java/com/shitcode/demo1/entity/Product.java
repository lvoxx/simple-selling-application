package com.shitcode.demo1.entity;

import java.math.BigDecimal;
import java.util.List;

import com.shitcode.demo1.helper.MediaUrlsConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "products") // Lowercase for PostgreSQL
public class Product extends AbstractAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 255)
    private String name; // GIN Index

    @Column(name = "in_stock_quantity", nullable = false)
    @Builder.Default
    private Integer inStockQuantity = 0;

    @Column(name = "in_sell_quantity", nullable = false)
    @Builder.Default
    private Integer inSellQuantity = 0;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal price = BigDecimal.valueOf(0.00);

    @Column(name = "currency", nullable = false, length = 3)
    @Builder.Default
    private String currency = "USD";

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false, foreignKey = @ForeignKey(name = "fk_product_category"))
    private Category category;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "discount_id", foreignKey = @ForeignKey(name = "fk_product_discount"))
    private Discount discount;

    @Convert(converter = MediaUrlsConverter.class)
    @Column(name = "images", length = 2000) // Ensure enough space for multiple paths
    private List<String> images;

    @Column(name = "video")
    private String video;

    @PrePersist
    public void prePersist() {
        if (currency == null) {
            currency = "USD";
        }
        if (inStockQuantity == null) {
            inStockQuantity = 0;
        }
        if (inSellQuantity == null) {
            inSellQuantity = 0;
        }
        if (price == null) {
            price = BigDecimal.valueOf(0.00);
        }
    }
}
