package com.shitcode.demo1.service.impl;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import com.shitcode.demo1.annotation.logging.LogCollector;
import com.shitcode.demo1.component.DatabaseLock;
import com.shitcode.demo1.dto.DiscountDTO.ManageRequest;
import com.shitcode.demo1.dto.DiscountDTO.ManageResponse;
import com.shitcode.demo1.entity.Discount;
import com.shitcode.demo1.exception.model.EntityExistsException;
import com.shitcode.demo1.helper.PaginationProvider;
import com.shitcode.demo1.mapper.DiscountMapper;
import com.shitcode.demo1.repository.DiscountRepository;
import com.shitcode.demo1.service.DiscountService;
import com.shitcode.demo1.utils.KeyLock;

import jakarta.transaction.Transactional;

@Service
@Transactional
@LogCollector
public class DiscountServiceImpl implements DiscountService {
    private final DiscountMapper discountMapper = DiscountMapper.INSTANCE;
    private final DiscountRepository discountRepository;
    private final DatabaseLock databaseLock;

    public DiscountServiceImpl(DiscountRepository discountRepository, DatabaseLock databaseLock) {
        this.discountRepository = discountRepository;
        this.databaseLock = databaseLock;
    }

    @Override
    @Cacheable(value = "discount-id", key = "#id")
    public ManageResponse findById(UUID id) {
        return discountMapper.toManageResponse(findEntityById(id));
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "discounts-title-expdate", allEntries = true),
    })
    public ManageResponse create(ManageRequest request) {
        Discount discount = discountMapper.toEntity(request);
        mustNotReturnEntityWhenFindingByTitle(request.getTitle());
        return discountMapper.toManageResponse(discountRepository.save(discount));
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "discount-id", key = "#id"),
            @CacheEvict(value = "discounts-title-expdate", allEntries = true),
    })
    public ManageResponse update(ManageRequest request, UUID id) {
        Discount discount = findEntityById(id);
        discount.setTitle(request.getTitle());
        discount.setTypes(request.getTypes());
        discount.setSalesPercentAmount(request.getSalesPercentAmount());
        discount.setExpDate(request.getExpDate());

        ManageResponse response = databaseLock.doAndLock(KeyLock.DISCOUNT,
                () -> discountMapper.toManageResponse(discountRepository.save(discount)));
        return response;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "discount-id", key = "#id"),
            @CacheEvict(value = "discounts-title-expdate", allEntries = true),
    })
    public void delete(UUID id) {
        findEntityById(id);
        discountRepository.deleteById(id);
    }

    @Override
    public void removeExpiredDiscountsFromProducts() {
        discountRepository.removeExpiredDiscountsFromProducts();
    }

    @Override
    @Cacheable(value = "discounts-title-expdate", key = "T(String).valueOf(#page) + '-' + T(String).valueOf(#size) + '-' + (#sort ?: 'default') + '-' + T(String).valueOf(#asc)")
    public Page<ManageResponse> findByTitleAndExpDateBetween(int page, int size, @Nullable String sort, boolean asc,
            String title, OffsetDateTime startDate, OffsetDateTime endDate) {
        Pageable pageable = PaginationProvider.build(page, size, sort, asc);
        Page<Discount> res = discountRepository.findByTitleAndExpDateBetween(title, startDate, endDate, pageable);
        return res.map(discountMapper::toManageResponse);
    }

    @Override
    public Discount findEntityById(UUID id) {
        return discountRepository.findById(id)
                .orElseThrow(() -> new EntityExistsException("{exception.entity-not-found.discount-id}"));
    }

    @Override
    public Discount findEntityByTitle(String title) {
        return discountRepository.findEntityByTitle(title)
                .orElseThrow(() -> new EntityExistsException("{exception.entity-not-found.discount-title}"));
    }

    @Override
    public List<Discount> findEntitiesByExpDateBetween(OffsetDateTime startDate, OffsetDateTime endDate) {
        return discountRepository.findByExpDateBetween(startDate, endDate);
    }

    private void mustNotReturnEntityWhenFindingByTitle(String title) {
        discountRepository.findEntityByTitle(title)
                .ifPresent(d -> new EntityExistsException("{exception.entity-exists.discount}"));
    }
}
