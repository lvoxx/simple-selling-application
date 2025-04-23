package com.shitcode.demo1.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shitcode.demo1.entity.RecipeProduct;

public interface RecipeProductRepository extends JpaRepository<RecipeProduct, Long> {

}
