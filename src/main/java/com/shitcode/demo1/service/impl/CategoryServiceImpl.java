package com.shitcode.demo1.service.impl;

import java.time.Instant;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shitcode.demo1.annotation.logging.LogCollector;
import com.shitcode.demo1.dto.CategoryDTO;
import com.shitcode.demo1.entity.Category;
import com.shitcode.demo1.exception.model.EntityExistsException;
import com.shitcode.demo1.exception.model.EntityNotFoundException;
import com.shitcode.demo1.helper.PaginationProvider;
import com.shitcode.demo1.mapper.CategoryMapper;
import com.shitcode.demo1.repository.CategoryRepository;
import com.shitcode.demo1.service.CategoryService;
import com.shitcode.demo1.utils.LoggingModel;
import com.shitcode.demo1.utils.cache.CategoryCacheType;

/**
 * Service implementation for managing Category entities.
 * <p>
 * This service provides operations to create, read, update, and delete categories,
 * as well as fetching categories with pagination support. Caching mechanisms
 * are applied to improve performance and reduce database load.
 * </p>
 */
@Service
@Transactional
@LogCollector(loggingModel = LoggingModel.SERVICE)
public class CategoryServiceImpl implements CategoryService {
    private static final Logger log = LoggerFactory.getLogger(CategoryServiceImpl.class);

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper = CategoryMapper.INSTANCE;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Retrieves a paginated list of categories.
     *
     * @param page the page number (zero-based index)
     * @param size the number of categories per page
     * @param sort optional sorting parameter
     * @param asc  true for ascending order, false for descending
     * @return a paginated list of categories
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CategoryCacheType.Fields.CATEGORIES, key = "T(String).valueOf(#page) + '-' + T(String).valueOf(#size) + '-' + (#sort ?: 'default') + '-' + T(String).valueOf(#asc)")
    public Page<CategoryDTO.Response> findCategoryWithPagination(int page, int size, @Nullable String sort, boolean asc) {
        Pageable pageable = PaginationProvider.build(page, size, sort, asc);
        Page<Category> res = Optional.ofNullable(categoryRepository.findPagedCategories(pageable)).orElse(Page.empty());
        log.debug("Return Category Page with load size {} at {}", res.getTotalElements(), Instant.now());
        return res.map(categoryMapper::toCategoryResponse);
    }

    /**
     * Finds a category by its unique ID.
     *
     * @param id the ID of the category
     * @return the category details
     * @throws EntityNotFoundException if the category does not exist
     */
    @Override
    @Cacheable(value = CategoryCacheType.Fields.CATEGORY_ID, key = "#id")
    @Transactional(readOnly = true)
    public CategoryDTO.Response findCategoryById(Long id) {
        Category res = findCategoryEntityById(id);
        return categoryMapper.toCategoryResponse(res);
    }

    /**
     * Finds a category by its unique name.
     *
     * @param name the name of the category
     * @return the category details
     * @throws EntityNotFoundException if the category does not exist
     */
    @Override
    @Cacheable(value = CategoryCacheType.Fields.CATEGORY_NAME, key = "#name")
    @Transactional(readOnly = true)
    public CategoryDTO.Response findCategoryByName(String name) {
        Category res = findCategoryEntityByName(name);
        return categoryMapper.toCategoryResponse(res);
    }

    /**
     * Creates a new category.
     *
     * @param req the category request DTO
     * @return the created category details
     * @throws EntityExistsException if a category with the same name already exists
     */
    @Override
    @CacheEvict(value = CategoryCacheType.Fields.CATEGORIES, allEntries = true)
    public CategoryDTO.Response createCategory(CategoryDTO.Request req) {
        doNotReturnCategoryByRequest(req);
        return categoryMapper.toCategoryResponse(categoryRepository.save(categoryMapper.toCategory(req)));
    }

    /**
     * Updates an existing category.
     *
     * @param req the category request DTO containing updated details
     * @param id  the ID of the category to update
     * @return the updated category details
     * @throws EntityNotFoundException if the category does not exist
     */
    @Override
    @Caching(evict = {
            @CacheEvict(value = CategoryCacheType.Fields.CATEGORIES, allEntries = true),
            @CacheEvict(value = CategoryCacheType.Fields.CATEGORY_ID, key = "#id"),
            @CacheEvict(value = CategoryCacheType.Fields.CATEGORY_NAME, key = "#req.name")
    })
    public CategoryDTO.Response updateCategory(CategoryDTO.Request req, Long id) {
        Category res = findCategoryEntityById(id);
        res.setName(req.getName());
        return categoryMapper.toCategoryResponse(categoryRepository.save(res));
    }

    /**
     * Deletes a category by its ID.
     *
     * @param id the ID of the category to delete
     * @throws EntityNotFoundException if the category does not exist
     */
    @Override
    @Caching(evict = {
            @CacheEvict(value = CategoryCacheType.Fields.CATEGORIES),
            @CacheEvict(value = CategoryCacheType.Fields.CATEGORY_ID, allEntries = true),
            @CacheEvict(value = CategoryCacheType.Fields.CATEGORY_NAME, allEntries = true)
    })
    public void deleteCategoryById(Long id) {
        findCategoryEntityById(id);
        categoryRepository.deleteById(id);
    }

    /**
     * Retrieves a category entity by its ID.
     *
     * @param id the ID of the category
     * @return the category entity
     * @throws EntityNotFoundException if the category does not exist
     */
    @Override
    public Category findCategoryEntityById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("{exception.entity-not-found.category-id}", id)));
    }

    /**
     * Retrieves a category entity by its name.
     *
     * @param name the name of the category
     * @return the category entity
     * @throws EntityNotFoundException if the category does not exist
     */
    @Override
    public Category findCategoryEntityByName(String name) {
        return categoryRepository.findByName(name)
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                String.format("{exception.entity-not-found.category-name}", name)));
    }

    /**
     * Validates that a category with the given name does not already exist.
     *
     * @param req the category request DTO
     * @throws EntityExistsException if a category with the same name already exists
     */
    private void doNotReturnCategoryByRequest(CategoryDTO.Request req) {
        categoryRepository.findByName(req.getName()).ifPresent(t -> {
            throw new EntityExistsException(String.format("{exception.entity-exists.category}", req.getName()));
        });
    }
}
