package com.shitcode.demo1.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import com.shitcode.demo1.dto.CategoryDTO;
import com.shitcode.demo1.entity.Category;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface CategoryMapper {
        CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

        // Map CategoryRequest to Category
        @Mappings({
                        @Mapping(target = "id", ignore = true),
                        @Mapping(target = "products", ignore = true)
        })
        Category toCategory(CategoryDTO.Request request);

        // Map CategoryRequest to Category
        @Mappings({
                        @Mapping(target = "products", ignore = true)
        })
        Category toCategory(CategoryDTO.Response response);

        // Map Category to CategoryResponse
        @Mappings({
                        @Mapping(target = "id", source = "id"),
                        @Mapping(target = "name", source = "name")
        })
        CategoryDTO.Response toCategoryResponse(Category category);
}
