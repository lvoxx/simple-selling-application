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
import com.shitcode.demo1.component.DatabaseLock;
import com.shitcode.demo1.dto.DiscountDTO.ApplyToProductsRequest;
import com.shitcode.demo1.dto.DiscountDTO.ApplyToProductsResponse;
import com.shitcode.demo1.dto.ProductDTO.AdminResponse;
import com.shitcode.demo1.dto.ProductDTO.InSellResponse;
import com.shitcode.demo1.dto.ProductDTO.Request;
import com.shitcode.demo1.entity.Category;
import com.shitcode.demo1.entity.Product;
import com.shitcode.demo1.exception.model.EntityExistsException;
import com.shitcode.demo1.exception.model.EntityNotFoundException;
import com.shitcode.demo1.helper.PaginationProvider;
import com.shitcode.demo1.mapper.ProductMapper;
import com.shitcode.demo1.repository.ProductRepository;
import com.shitcode.demo1.service.CategoryService;
import com.shitcode.demo1.service.ProductService;
import com.shitcode.demo1.utils.KeyLock;

@Service
@Transactional
@LogCollector
public class ProductServiceImpl implements ProductService {
    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);
    private final ProductMapper productMapper = ProductMapper.INSTANCE;

    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final DatabaseLock databaseLock;

    public ProductServiceImpl(ProductRepository productRepository, CategoryService categoryService,
            DatabaseLock databaseLock) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
        this.databaseLock = databaseLock;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "insell-products", key = "T(String).valueOf(#page) + '-' + T(String).valueOf(#size) + '-' + (#sort ?: 'default') + '-' + T(String).valueOf(#asc)")
    public Page<InSellResponse> findInSellWithPagination(int page, int size, @Nullable String sort, boolean asc) {
        log.debug("Fetching in-sell products with page: {}, size: {}, sort: {}, asc: {}", page, size, sort, asc);

        Pageable pageable = PaginationProvider.build(page, size, sort, asc);
        Page<Product> res = Optional.ofNullable(productRepository.findPagedAllProductsWithCategory(pageable))
                .orElse(Page.empty());

        log.debug("Fetched {} in-sell products at {}", res.getTotalElements(), Instant.now());

        return res.map(productMapper::toProductInSellResponse);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "admin-products", key = "T(String).valueOf(#page) + '-' + T(String).valueOf(#size) + '-' + (#sort ?: 'default') + '-' + T(String).valueOf(#asc)")
    public Page<AdminResponse> findAdminWithPagination(int page, int size, @Nullable String sort, boolean asc) {
        log.debug("Fetching admin products with page: {}, size: {}, sort: {}, asc: {}", page, size, sort, asc);

        Pageable pageable = PaginationProvider.build(page, size, sort, asc);
        Page<Product> res = Optional.ofNullable(productRepository.findPagedAllProductsWithCategory(pageable))
                .orElse(Page.empty());

        log.debug("Fetched {} admin products at {}", res.getTotalElements(), Instant.now());

        return res.map(productMapper::toProductAdminResponse);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "admin-products", allEntries = true),
            @CacheEvict(value = "insell-products", allEntries = true)
    })
    public AdminResponse create(Request request) {
        log.debug("Creating product with name: {}", request.getName());

        Optional.ofNullable(findEntityWithName(request.getName())).ifPresent(p -> {
            log.warn("Product with name '{}' already exists.", request.getName());
            throw new EntityExistsException("{exception.entity-exists.product}");
        });

        log.debug("Fetching category with ID: {}", request.getCategoryId());
        Category category = categoryService.findCategoryEntityById(request.getCategoryId());

        log.debug("Mapping request to Product entity.");
        Product product = productMapper.toProduct(request);
        product.setCategory(category);

        log.debug("Saving product: {}", product);
        Product savedProduct = productRepository.save(product);

        log.debug("Returning mapped AdminResponse.");
        return productMapper.toProductAdminResponse(savedProduct);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "admin-products", allEntries = true),
            @CacheEvict(value = "insell-products", allEntries = true),
            @CacheEvict(value = "admin-product-id", key = "#id"),
            @CacheEvict(value = "insell-product-id", key = "#id"),
            @CacheEvict(value = "admin-product-name", key = "#req.name"),
            @CacheEvict(value = "insell-product-name", key = "#req.name")
    })
    public AdminResponse update(Request request, Long id) {
        log.debug("Updating product with ID: {}", id);

        Product product = findEntityWithId(id);
        Category category = categoryService.findCategoryEntityById(request.getCategoryId());
        product.setCategory(category);
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setInSellQuantity(request.getInSellQuantity());
        product.setInStockQuantity(request.getInStockQuantity());

        AdminResponse response = databaseLock.doAndLock(KeyLock.PRODUCT,
                () -> productMapper.toProductAdminResponse(productRepository.save(product)));

        log.debug("Updated product successfully: {}", response);
        return response;
    }

    

    @Override
    public ApplyToProductsResponse putDiscountToProducts(ApplyToProductsRequest requests) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "admin-products", allEntries = true),
            @CacheEvict(value = "insell-products", allEntries = true),
            @CacheEvict(value = "admin-product-id", key = "#id"),
            @CacheEvict(value = "insell-product-id", key = "#id"),
            @CacheEvict(value = "admin-product-name", key = "#req.name"),
            @CacheEvict(value = "insell-product-name", key = "#req.name")
    })
    public InSellResponse sellWith(Integer quantity, Long id) {
        log.debug("Selling {} units of product ID: {}", quantity, id);

        Product product = findEntityWithId(id);
        product.setInSellQuantity(product.getInSellQuantity() - quantity);

        InSellResponse response = productMapper.toProductInSellResponse(productRepository.save(product));

        log.debug("Updated sell quantity. New response: {}", response);
        return response;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "admin-products", allEntries = true),
            @CacheEvict(value = "insell-products", allEntries = true),
            @CacheEvict(value = "admin-product-id", key = "#id"),
            @CacheEvict(value = "insell-product-id", key = "#id"),
            @CacheEvict(value = "admin-product-name", key = "#req.name"),
            @CacheEvict(value = "insell-product-name", key = "#req.name")
    })
    public InSellResponse importWith(Integer quantity, Long id) {
        log.debug("Importing {} units for product ID: {}", quantity, id);

        Product product = findEntityWithId(id);
        product.setInStockQuantity(product.getInStockQuantity() + quantity);

        InSellResponse response = productMapper.toProductInSellResponse(productRepository.save(product));

        log.debug("Updated stock quantity. New response: {}", response);
        return response;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "admin-products", allEntries = true),
            @CacheEvict(value = "insell-products", allEntries = true),
            @CacheEvict(value = "admin-product-id", key = "#id"),
            @CacheEvict(value = "insell-product-id", key = "#id"),
            @CacheEvict(value = "admin-product-name", key = "#req.name"),
            @CacheEvict(value = "insell-product-name", key = "#req.name")
    })
    public InSellResponse exportWith(Integer quantity, Long id) {
        log.debug("Exporting {} units from product ID: {}", quantity, id);

        Product product = findEntityWithId(id);
        product.setInStockQuantity(product.getInStockQuantity() - quantity);

        InSellResponse response = productMapper.toProductInSellResponse(productRepository.save(product));

        log.debug("Updated stock quantity. New response: {}", response);
        return response;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "admin-products", allEntries = true),
            @CacheEvict(value = "insell-products", allEntries = true),
            @CacheEvict(value = "admin-product-id", key = "#id"),
            @CacheEvict(value = "insell-product-id", key = "#id"),
            @CacheEvict(value = "admin-product-name", key = "#req.name"),
            @CacheEvict(value = "insell-product-name", key = "#req.name")
    })
    public void delete(Long id) {
        log.debug("Deleting product with ID: {}", id);

        findEntityWithId(id);
        productRepository.deleteById(id);

        log.debug("Deleted product successfully: ID {}", id);
    }

    @Override
    @Cacheable(value = "admin-products-id", key = "#id")
    @Transactional(readOnly = true)
    public AdminResponse findAdminProductWithId(Long id) {
        return productMapper.toProductAdminResponse(findEntityWithId(id));
    }

    @Override
    @Cacheable(value = "admin-products-name", key = "#name")
    @Transactional(readOnly = true)
    public AdminResponse findAdminProductWithName(String name) {
        return productMapper.toProductAdminResponse(findEntityWithName(name));
    }

    @Override
    @Cacheable(value = "insell-products-id", key = "#id")
    @Transactional(readOnly = true)
    public InSellResponse findInSellProductWithId(Long id) {
        return productMapper.toProductInSellResponse(findEntityWithId(id));
    }

    @Override
    @Cacheable(value = "insell-products-name", key = "#name")
    @Transactional(readOnly = true)
    public InSellResponse findInSellWithName(String name) {
        return productMapper.toProductInSellResponse(findEntityWithName(name));
    }

    @Override
    public Product findEntityWithId(Long id) {
        return productRepository.findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                String.format("{exception.entity-not-found.product-id}", id)));
    }

    @Override
    public Product findEntityWithName(String name) {
        return productRepository.findByName(name)
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                String.format("{exception.entity-not-found.product-name}", name)));
    }
}
