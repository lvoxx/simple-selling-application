package com.shitcode.demo1.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import com.shitcode.demo1.dto.ProductDTO;
import com.shitcode.demo1.entity.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {
        ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

        // Map ProductRequest to Product
        @Mappings({
                        @Mapping(target = "id", ignore = true), // Ignore ID during request-to-entity mapping
                        @Mapping(target = "category.id", ignore = true) // Map categoryId to Category.id
        })
        Product toProduct(ProductDTO.Request request);

        // Map Product to ProductInSellResponse
        @Mappings({
                        @Mapping(target = "availableQuatity", expression = "java(product.getInStockQuantity() - product.getInSellQuantity())"),
        })
        ProductDTO.InSellResponse toProductInSellResponse(Product product);

        ProductDTO.AdminResponse toProductAdminResponse(Product product);
}