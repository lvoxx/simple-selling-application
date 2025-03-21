package com.shitcode.demo1.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.data.domain.Page;
import org.springframework.lang.Nullable;

import com.shitcode.demo1.annotation.validation.GreaterOrEquals;
import com.shitcode.demo1.dto.DiscountDTO;
import com.shitcode.demo1.entity.Discount;

public interface DiscountService {
    DiscountDTO.DiscountDetailsResponse findById(UUID id);

    DiscountDTO.ManageResponse create(DiscountDTO.ManageRequest request);

    DiscountDTO.ManageResponse update(DiscountDTO.ManageRequest request, UUID id);

    void delete(UUID id);

    void removeExpiredDiscountsFromProducts(UUID id);

    Page<DiscountDTO.ManageResponse> findByTitleAndExpDateBetween(
            @GreaterOrEquals(value = 1, message = "Page index must be greater than or equal to 1") int page,
            @GreaterOrEquals(value = 1, message = "Page size must be greater than or equal to 1") int size,
            @Nullable String sort, @DefaultValue("false") boolean asc,
            @DefaultValue("") String title,
            OffsetDateTime startDate, OffsetDateTime endDate);

    Discount findEntityByTitle(String title);

    Discount findEntityById(UUID id);

    Discount findDetailEntityById(UUID id);

    List<Discount> findEntitiesByExpDateBetween(OffsetDateTime startDate, OffsetDateTime endDate);
}