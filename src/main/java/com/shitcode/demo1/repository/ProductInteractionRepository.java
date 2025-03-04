package com.shitcode.demo1.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shitcode.demo1.entity.ProductInteraction;

@Repository
public interface ProductInteractionRepository extends JpaRepository<ProductInteraction, UUID> {
    ProductInteraction findByUserId(Long userId);

    ProductInteraction findByProductId(Long productId);
}
