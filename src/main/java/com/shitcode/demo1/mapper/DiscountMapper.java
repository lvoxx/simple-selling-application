package com.shitcode.demo1.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import com.shitcode.demo1.dto.DiscountDTO;
import com.shitcode.demo1.entity.Discount;
import com.shitcode.demo1.entity.Product;

@Mapper(componentModel = "spring")
public interface DiscountMapper {

        DiscountMapper INSTANCE = Mappers.getMapper(DiscountMapper.class);

        // Mapping from DTO to Entity
        @Mapping(target = "id", ignore = true)
        @Mapping(target = "products", ignore = true)
        Discount toEntity(DiscountDTO.ManageRequest dto);

        // Mapping from Entity to DTO
        DiscountDTO.ManageResponse toManageResponse(Discount entity);

        // List mappings
        List<DiscountDTO.ManageResponse> toManageResponseList(List<Discount> entities);

        // Mapping for ApplyToProductsResponse
        @Mappings({
                        @Mapping(target = "productIds", expression = "java(mapProductIds(discount.getProducts()))"),
                        @Mapping(target = "discountId", source = "id")
        })
        DiscountDTO.ApplyToProductsResponse toApplyToProductsResponse(Discount discount);

        // Helper method to extract product IDs
        default List<Long> mapProductIds(List<Product> products) {
                return products.stream().map(Product::getId).collect(Collectors.toList());
        }
}