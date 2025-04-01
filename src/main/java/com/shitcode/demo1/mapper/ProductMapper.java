package com.shitcode.demo1.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import com.shitcode.demo1.dto.DiscountDTO.ApplyToProductsRequest;
import com.shitcode.demo1.dto.DiscountDTO.ApplyToProductsResponse;
import com.shitcode.demo1.dto.ProductDTO;
import com.shitcode.demo1.entity.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {
        ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

        // Map ProductRequest to Product
        @Mappings({
                        @Mapping(target = "id", ignore = true), // Ignore ID during request-to-entity mapping
                        @Mapping(target = "discount", ignore = true), // Ignore ID during request-to-entity mapping
                        @Mapping(target = "category.id", ignore = true), // Map categoryId to Category.id
                        @Mapping(target = "video", ignore = true),
                        @Mapping(target = "images", ignore = true)
        })
        Product toProduct(ProductDTO.Request request);

        // Map Product to ProductInSellResponse
        @Mappings({
                        @Mapping(target = "availableQuatity", expression = "java(product.getInStockQuantity() - product.getInSellQuantity())"),
                        @Mapping(target = "imageUrls", source = "images"),
                        @Mapping(target = "videoUrl", source = "video")
        })
        ProductDTO.InSellResponse toProductInSellResponse(Product product);

        @Mappings({
                        @Mapping(target = "imageUrls", source = "images"),
                        @Mapping(target = "videoUrl", source = "video")
        })
        ProductDTO.AdminResponse toProductAdminResponse(Product product);

        ApplyToProductsResponse toApplyDiscountResponse(ApplyToProductsRequest request);

        ApplyToProductsRequest toApplyDiscountRequest(ApplyToProductsResponse response);

        ProductDTO.DiscountResponse toDiscountResponse(Product product);
}