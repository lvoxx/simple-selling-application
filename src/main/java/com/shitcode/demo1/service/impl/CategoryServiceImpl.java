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

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "categories", key = "T(String).valueOf(#page) + '-' + T(String).valueOf(#size) + '-' + (#sort ?: 'default') + '-' + T(String).valueOf(#asc)")
    public Page<CategoryDTO.Response> findCategoryWithPagination(int page, int size, @Nullable String sort,
            boolean asc) {
        Pageable pageable = PaginationProvider.build(page, size, sort, asc);
        ;

        Page<Category> res = Optional.ofNullable(categoryRepository.findPagedCategories(pageable)).orElse(Page.empty());
        log.debug("Return Category Page with load size {} at {}", res.getTotalElements(), Instant.now());

        return res.map(c -> categoryMapper.toCategoryResponse(c));
    }

    @Override
    @Cacheable(value = "category-id", key = "#id")
    @Transactional(readOnly = true)
    public CategoryDTO.Response findCategoryById(Long id) {
        Category res = findCategoryEntityById(id);
        return categoryMapper.toCategoryResponse(res);
    }

    @Override
    @Cacheable(value = "category-name", key = "#name")
    @Transactional(readOnly = true)
    public CategoryDTO.Response findCategoryByName(String name) {
        Category res = findCategoryEntityByName(name);
        return categoryMapper.toCategoryResponse(res);
    }

    @Override
    @CacheEvict(value = "categories", allEntries = true)
    public CategoryDTO.Response createCategory(CategoryDTO.Request req) {
        doNotReturnCategoryByRequest(req);
        return categoryMapper.toCategoryResponse(categoryRepository.save(categoryMapper.toCategory(req)));
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "categories", allEntries = true),
            @CacheEvict(value = "category-id", key = "#id"),
            @CacheEvict(value = "category-name", key = "#req.name")
    })
    public CategoryDTO.Response updateCategory(CategoryDTO.Request req, Long id) {
        Category res = findCategoryEntityById(id);
        res.setName(req.getName());
        return categoryMapper.toCategoryResponse(categoryRepository.save(res));
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "categories"),
            @CacheEvict(value = "category-id", allEntries = true),
            @CacheEvict(value = "category-name", allEntries = true)
    })
    public void deleteCategoryById(Long id) {
        findCategoryEntityById(id);
        categoryRepository.deleteById(id);
    }

    @Override
    public Category findCategoryEntityById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("{exception.entity-not-found.category-id}", id)));
    }

    @Override
    public Category findCategoryEntityByName(String name) {
        return categoryRepository.findByName(name)
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                String.format("{exception.entity-not-found.category-name}", name)));
    }

    private void doNotReturnCategoryByRequest(CategoryDTO.Request req) {
        categoryRepository.findByName(req.getName()).ifPresent(t -> {
            throw new EntityExistsException(String.format("{exception.entity-exists.category}", req.getName()));
        });
    }
}
