package com.shitcode.demo1.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
@Table(name = "recipe")
public class Recipe extends AbstractAuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, updatable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private RecipeStatus status = RecipeStatus.PENDING;

    @Column(length = 1000, updatable = false)
    private String description;

    @Column(nullable = false, updatable = false, precision = 2)
    private Double total;

    @Column(nullable = false, updatable = false)
    private String username;

    @Column(nullable = false, updatable = false)
    private String shippingAddress;

    @Column(nullable = false, updatable = false, precision = 2)
    private Double shippingFee;

    @OneToMany(mappedBy = "recipe", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RecipeProduct> recipeProducts = new ArrayList<>();

    @OneToOne(mappedBy = "recipe", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private PaypalTransaction paypalTransaction;

    public enum RecipeStatus {
        PENDING,
        SUCCESS,
        CANCELED,
        ERROR
    }

    public void addRecipeProduct(RecipeProduct recipeProduct) {
        recipeProduct.setRecipe(this);
        recipeProducts.add(recipeProduct);
    }

    public void removeRecipeProduct(RecipeProduct recipeProduct) {
        recipeProduct.setRecipe(null);
        recipeProducts.remove(recipeProduct);
    }
}