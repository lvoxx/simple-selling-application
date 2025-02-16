package com.shitcode.demo1.service;

import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.data.domain.Page;
import org.springframework.lang.Nullable;

import com.shitcode.demo1.annotation.validation.GreaterOrEquals;
import com.shitcode.demo1.dto.CategoryDTO;
import com.shitcode.demo1.entity.Category;
import com.shitcode.demo1.exception.model.EntityNotFoundException;

/**
 * Service interface for handling Category-related operations.
 * 
 * @author Lvoxx
 */
public interface CategoryService {
        Page<CategoryDTO.Response> findCategoryWithPagination(
                        @GreaterOrEquals(value = 1, message = "Page index must be greater than or equal to 1") int page,
                        @GreaterOrEquals(value = 1, message = "Page size must be greater than or equal to 1") int size,
                        @Nullable String sort, @DefaultValue("false") boolean asc);

        CategoryDTO.Response findCategoryById(
                        @GreaterOrEquals(value = 1L, message = "Category Id must be greater than or equal to 1") Long id);

        CategoryDTO.Response findCategoryByName(String name);

        CategoryDTO.Response createCategory(CategoryDTO.Request req);

        CategoryDTO.Response updateCategory(CategoryDTO.Request req,
                        @GreaterOrEquals(value = 1L, message = "Category Id must be greater than or equal to 1") Long id);

        void deleteCategoryById(
                        @GreaterOrEquals(value = 1L, message = "Category Id must be greater than or equal to 1") Long id);

        // ! START FOR DEV
        /**
         * Finds a Category entity by its ID.
         * <p>
         * This method retrieves a category entity based on the provided ID.
         * The ID must be greater than or equal to 1.
         * </p>
         * <p>
         * <b>Note:</b> These methods are intended for development use only.
         * </p>
         *
         * @param id the unique identifier of the category
         * @return the Category entity if found
         * @throws EntityNotFoundException if no category is found with the given ID
         */
        Category findCategoryEntityById(
                        @GreaterOrEquals(value = 1L, message = "Category Id must be greater than or equal to 1") Long id);

        /**
         * Finds a Category entity by its name.
         * <p>
         * This method retrieves a category entity based on the provided category name.
         * <b> Note:</b> This is intended for development use only.
         * </p>
         *
         * @param name the name of the category
         * @return the Category entity if found
         * @throws EntityNotFoundException if no category is found with the given name
         */
        Category findCategoryEntityByName(String name);
        // ! END FOR DEV
}
