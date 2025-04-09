package com.shitcode.demo1.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.data.domain.Page;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

import com.shitcode.demo1.annotation.validation.GreaterOrEquals;
import com.shitcode.demo1.dto.DiscountDTO;
import com.shitcode.demo1.dto.ProductDTO;
import com.shitcode.demo1.entity.Product;
import com.shitcode.demo1.exception.model.EntityNotFoundException;

/**
 * Service interface for retrieving Product entities.
 * 
 * @author Lvoxx
 */
public interface ProductService {
        Page<ProductDTO.InSellResponse> findInSellWithPagination(
                        @GreaterOrEquals(value = 1, message = "Page index must be greater than or equal to 1") int page,
                        @GreaterOrEquals(value = 1, message = "Page size must be greater than or equal to 1") int size,
                        @Nullable String sort, @DefaultValue("false") boolean asc);

        Page<ProductDTO.AdminResponse> findAdminWithPagination(
                        @GreaterOrEquals(value = 1, message = "Page index must be greater than or equal to 1") int page,
                        @GreaterOrEquals(value = 1, message = "Page size must be greater than or equal to 1") int size,
                        @Nullable String sort, @DefaultValue("false") boolean asc);

        ProductDTO.AdminResponse findAdminProductWithId(Long id);

        ProductDTO.InSellResponse findInSellProductWithId(Long id);

        ProductDTO.AdminResponse findAdminProductWithName(String name);

        ProductDTO.InSellResponse findInSellWithName(String name);

        ProductDTO.AdminResponse create(ProductDTO.CreateRequest jsonRequest, List<MultipartFile> images,
                        MultipartFile video)
                        throws FileNotFoundException, IOException;

        ProductDTO.AdminResponse update(ProductDTO.UpdateRequest request, List<MultipartFile> images,
                        MultipartFile video, Long id) throws FileNotFoundException, IOException;
                        
        DiscountDTO.ApplyToProductsResponse putDiscountToProducts(DiscountDTO.ApplyToProductsRequest requests);

        ProductDTO.InSellResponse sellWith(Integer quantity, Long id);

        ProductDTO.InSellResponse importWith(Integer quantity, Long id);

        ProductDTO.InSellResponse exportWith(Integer quantity, Long id);

        void delete(Long id);

        // ! START FOR DEV
        /**
         * Finds a product entity by its ID.
         * <p>
         * <b>Note:</b> These methods are intended for development use only. Use with
         * end-point services, dont cache it.
         * </p>
         * 
         * @param id the unique identifier of the product.
         * @return the {@link Product} entity if found.
         * @throws EntityNotFoundException if no product is found with the given ID
         */
        Product findEntityWithId(Long id);

        /**
         * Finds a product entity by its name.
         * <p>
         * <b>Note:</b> These methods are intended for development use only. Use with
         * end-point services, dont cache it.
         * </p>
         * 
         * @param name the name of the product.
         * @return the {@link Product} entity if found.
         * @throws EntityNotFoundException if no product is found with the given ID
         */
        Product findEntityWithName(String name);
        // ! END FOR DEV
}