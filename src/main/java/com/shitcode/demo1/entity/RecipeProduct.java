package com.shitcode.demo1.entity;

import com.shitcode.demo1.utils.DiscountType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "recipe_products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false, foreignKey = @ForeignKey(name = "fk_recipe_recipe_product"))
    private Recipe recipe;

    @Column(nullable = false)
    private Long categoryId;

    @Column(nullable = false)
    private String categoryName;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Double price;

    @Column
    private String discountName;

    @Column
    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    @Column
    private Double discountAmount;

    @Column(nullable = false)
    private Double subTotal;
}
