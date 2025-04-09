package com.shitcode.demo1.service.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.shitcode.demo1.annotation.logging.LogCollector;
import com.shitcode.demo1.component.DatabaseLock;
import com.shitcode.demo1.dto.DiscountDTO.ApplyToProductsRequest;
import com.shitcode.demo1.dto.DiscountDTO.ApplyToProductsResponse;
import com.shitcode.demo1.dto.ProductDTO;
import com.shitcode.demo1.dto.ProductDTO.AdminResponse;
import com.shitcode.demo1.dto.ProductDTO.InSellResponse;
import com.shitcode.demo1.dto.ProductInteractionDTO;
import com.shitcode.demo1.entity.Category;
import com.shitcode.demo1.entity.Product;
import com.shitcode.demo1.exception.model.EntityExistsException;
import com.shitcode.demo1.exception.model.EntityNotFoundException;
import com.shitcode.demo1.helper.PaginationProvider;
import com.shitcode.demo1.mapper.ProductMapper;
import com.shitcode.demo1.repository.ProductRepository;
import com.shitcode.demo1.service.CategoryService;
import com.shitcode.demo1.service.InterationEventService;
import com.shitcode.demo1.service.MediaService;
import com.shitcode.demo1.service.ProductService;
import com.shitcode.demo1.utils.KeyLock;
import com.shitcode.demo1.utils.LoggingModel;
import com.shitcode.demo1.utils.cache.ProductCacheType;

@Service
@Transactional
@LogCollector(loggingModel = LoggingModel.SERVICE)
public class ProductServiceImpl implements ProductService {
    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);
    private final ProductMapper productMapper = ProductMapper.INSTANCE;

    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final MediaService mediaService;
    private final InterationEventService interationEventService;
    private final DatabaseLock databaseLock;
    private final MessageSource messageSource;

    public ProductServiceImpl(ProductRepository productRepository, CategoryService categoryService,
            MediaService mediaService, InterationEventService interationEventService, DatabaseLock databaseLock,
            MessageSource messageSource) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
        this.mediaService = mediaService;
        this.interationEventService = interationEventService;
        this.databaseLock = databaseLock;
        this.messageSource = messageSource;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = ProductCacheType.Fields.INSELL_PRODUCTS, key = "T(String).valueOf(#page) + '-' + T(String).valueOf(#size) + '-' + (#sort ?: 'default') + '-' + T(String).valueOf(#asc)")
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
    @Cacheable(value = ProductCacheType.Fields.ADMIN_PRODUCTS, key = "T(String).valueOf(#page) + '-' + T(String).valueOf(#size) + '-' + (#sort ?: 'default') + '-' + T(String).valueOf(#asc)")
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
            @CacheEvict(value = ProductCacheType.Fields.ADMIN_PRODUCTS, allEntries = true),
            @CacheEvict(value = ProductCacheType.Fields.INSELL_PRODUCTS, allEntries = true)
    })
    public AdminResponse create(ProductDTO.CreateRequest jsonRequest, List<MultipartFile> images, MultipartFile video)
            throws FileNotFoundException, IOException {
        productRepository.findByName(jsonRequest.getName()).ifPresent(p -> {
            log.warn("Product with name '{}' already exists.", jsonRequest.getName());
            throw new EntityExistsException(messageSource.getMessage("exception.entity-exists.product",
                    new Object[] { jsonRequest.getName() }, Locale.getDefault()));
        });
        // Find Category
        Category category = categoryService.findCategoryEntityById(jsonRequest.getCategoryId());

        // Save media
        List<String> imageUrls = new ArrayList<>();
        for (MultipartFile image : images) {
            imageUrls.add(mediaService.saveImageFile(image));
        }
        String videoUrl = mediaService.saveVideoFile(video);

        // Set ref value
        Product product = productMapper.toProduct(jsonRequest);
        product.setCategory(category);
        product.setImages(imageUrls);
        product.setVideo(videoUrl);

        Product savedProduct = productRepository.save(product);

        return productMapper.toProductAdminResponse(savedProduct);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = ProductCacheType.Fields.ADMIN_PRODUCTS, allEntries = true),
            @CacheEvict(value = ProductCacheType.Fields.INSELL_PRODUCTS, allEntries = true),
            @CacheEvict(value = ProductCacheType.Fields.ADMIN_PRODUCT_ID, key = "#id"),
            @CacheEvict(value = ProductCacheType.Fields.INSELL_PRODUCT_ID, key = "#id"),
            @CacheEvict(value = ProductCacheType.Fields.ADMIN_PRODUCT_NAME, key = "#req.name"),
            @CacheEvict(value = ProductCacheType.Fields.INSELL_PRODUCT_NAME, key = "#req.name")
    })
    public AdminResponse update(ProductDTO.UpdateRequest request, List<MultipartFile> images, MultipartFile video,
            Long id) throws FileNotFoundException, IOException {
        log.debug("Updating product with ID: {}", id);

        Product product = findEntityWithId(id);
        Category category = categoryService.findCategoryEntityById(request.getCategoryId());
        product.setCategory(category);
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setInSellQuantity(request.getInSellQuantity());
        product.setInStockQuantity(request.getInStockQuantity());

        if (request.getUpdateOldImageUrlToNewImageFileName() != null && images != null) {
            product.setImages(updateImagesFromProduct(request.getUpdateOldImageUrlToNewImageFileName(), product.getImages(), images));
        }
        if (video != null) {
            product.setVideo(updateVideoFromProduct(product.getVideo(), video));
        }

        AdminResponse response = databaseLock.doAndLock(KeyLock.PRODUCT, id,
                () -> productMapper.toProductAdminResponse(productRepository.save(product)));

        log.debug("Updated product successfully: {}", response);
        return response;
    }

    private String updateVideoFromProduct(String oldVideoUrl,
            MultipartFile newVideo) throws IOException {
        mediaService.deleteFile(oldVideoUrl);
        return mediaService.saveVideoFile(newVideo);
    }

    private List<String> updateImagesFromProduct(Map<String, String> oldImageUrlsAndNewImageNames,
            List<String> oldImageUrls,
            List<MultipartFile> newImages)
            throws IOException {
        // Build a new oldImageUrls
        List<String> newOldImageUrls = new ArrayList<>(oldImageUrls);
        // Build a map for quick lookup: image name -> MultipartFile
        Map<String, MultipartFile> newImageMap = newImages.stream()
                .collect(Collectors.toMap(MultipartFile::getOriginalFilename, image -> image));

        for (Map.Entry<String, String> entry : oldImageUrlsAndNewImageNames.entrySet()) {
            String oldImageUrl = entry.getKey();
            String newImageName = entry.getValue();

            // Remove old URL and delete the old image
            newOldImageUrls.remove(oldImageUrl);
            mediaService.deleteFile(oldImageUrl);

            // Get corresponding new image file
            MultipartFile newImage = newImageMap.get(newImageName);
            if (newImage == null) {
                throw new FileNotFoundException(messageSource.getMessage(
                        "exception.entity-not-found.product-media",
                        new Object[] { newImageName }, Locale.getDefault()));
            }

            // Save new image and update list
            String newImageUrl = mediaService.saveImageFile(newImage);
            newOldImageUrls.add(newImageUrl);
        }

        return newOldImageUrls;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = ProductCacheType.Fields.ADMIN_PRODUCTS, allEntries = true),
            @CacheEvict(value = ProductCacheType.Fields.INSELL_PRODUCTS, allEntries = true),
            @CacheEvict(value = ProductCacheType.Fields.ADMIN_PRODUCT_ID, key = "#requests.productIds"),
            @CacheEvict(value = ProductCacheType.Fields.INSELL_PRODUCT_ID, key = "#requests.productIds")
    })
    public ApplyToProductsResponse putDiscountToProducts(ApplyToProductsRequest requests) {
        requests.getProductIds().forEach(pId -> {
            productRepository.updateDiscountByProductId(pId, requests.getDiscountId());
        });
        return productMapper.toApplyDiscountResponse(requests);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = ProductCacheType.Fields.ADMIN_PRODUCTS, allEntries = true),
            @CacheEvict(value = ProductCacheType.Fields.INSELL_PRODUCTS, allEntries = true),
            @CacheEvict(value = ProductCacheType.Fields.ADMIN_PRODUCT_ID, key = "#id"),
            @CacheEvict(value = ProductCacheType.Fields.INSELL_PRODUCT_ID, key = "#id")
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
            @CacheEvict(value = ProductCacheType.Fields.ADMIN_PRODUCTS, allEntries = true),
            @CacheEvict(value = ProductCacheType.Fields.INSELL_PRODUCTS, allEntries = true),
            @CacheEvict(value = ProductCacheType.Fields.ADMIN_PRODUCT_ID, key = "#id"),
            @CacheEvict(value = ProductCacheType.Fields.INSELL_PRODUCT_ID, key = "#id")
    })
    public synchronized InSellResponse importWith(Integer quantity, Long id) {
        log.debug("Importing {} units for product ID: {}", quantity, id);

        Product product = findEntityWithId(id);
        product.setInStockQuantity(product.getInStockQuantity() + quantity);

        InSellResponse response = productMapper.toProductInSellResponse(productRepository.save(product));

        log.debug("Updated stock quantity. New response: {}", response);
        return response;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = ProductCacheType.Fields.ADMIN_PRODUCTS, allEntries = true),
            @CacheEvict(value = ProductCacheType.Fields.INSELL_PRODUCTS, allEntries = true),
            @CacheEvict(value = ProductCacheType.Fields.ADMIN_PRODUCT_ID, key = "#id"),
            @CacheEvict(value = ProductCacheType.Fields.INSELL_PRODUCT_ID, key = "#id")
    })
    public synchronized InSellResponse exportWith(Integer quantity, Long id) {
        log.debug("Exporting {} units from product ID: {}", quantity, id);

        Product product = findEntityWithId(id);
        product.setInStockQuantity(product.getInStockQuantity() - quantity);

        InSellResponse response = productMapper.toProductInSellResponse(productRepository.save(product));

        log.debug("Updated stock quantity. New response: {}", response);
        return response;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = ProductCacheType.Fields.ADMIN_PRODUCTS, allEntries = true),
            @CacheEvict(value = ProductCacheType.Fields.INSELL_PRODUCTS, allEntries = true),
            @CacheEvict(value = ProductCacheType.Fields.ADMIN_PRODUCT_ID, key = "#id"),
            @CacheEvict(value = ProductCacheType.Fields.INSELL_PRODUCT_ID, key = "#id")
    })
    public synchronized void delete(Long id) {
        log.debug("Deleting product with ID: {}", id);

        findEntityWithId(id);
        productRepository.deleteById(id);

        log.debug("Deleted product successfully: ID {}", id);
    }

    @Override
    @Cacheable(value = ProductCacheType.Fields.ADMIN_PRODUCT_ID, key = "#id")
    @Transactional(readOnly = true)
    public AdminResponse findAdminProductWithId(Long id) {
        return productMapper.toProductAdminResponse(findEntityWithId(id));
    }

    @Override
    @Cacheable(value = ProductCacheType.Fields.ADMIN_PRODUCT_NAME, key = "#name")
    @Transactional(readOnly = true)
    public AdminResponse findAdminProductWithName(String name) {
        return productMapper.toProductAdminResponse(findEntityWithName(name));
    }

    @Override
    @Cacheable(value = ProductCacheType.Fields.INSELL_PRODUCT_ID, key = "#id")
    @Transactional(readOnly = true)
    public InSellResponse findInSellProductWithId(Long id) {
        interationEventService.recordNewEvent(new ProductInteractionDTO.Request(id));
        return productMapper.toProductInSellResponse(findEntityWithId(id));
    }

    @Override
    @Cacheable(value = ProductCacheType.Fields.INSELL_PRODUCT_NAME, key = "#name")
    @Transactional(readOnly = true)
    public InSellResponse findInSellWithName(String name) {
        Product product = findEntityWithName(name);
        interationEventService.recordNewEvent(new ProductInteractionDTO.Request(product.getId()));
        return productMapper.toProductInSellResponse(product);
    }

    @Override
    public Product findEntityWithId(Long id) {
        return productRepository.findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                messageSource.getMessage("exception.entity-not-found.product-id",
                                        new Object[] { id }, Locale.getDefault())));
    }

    @Override
    public Product findEntityWithName(String name) {
        return productRepository.findByName(name)
                .orElseThrow(
                        () -> new EntityNotFoundException(
                                messageSource.getMessage("exception.entity-not-found.product-name",
                                        new Object[] { name }, Locale.getDefault())));
    }
}
