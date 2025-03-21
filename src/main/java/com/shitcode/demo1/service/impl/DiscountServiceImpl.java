package com.shitcode.demo1.service.impl;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
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
import com.shitcode.demo1.dto.DiscountDTO.DiscountDetailsResponse;
import com.shitcode.demo1.dto.DiscountDTO.ManageRequest;
import com.shitcode.demo1.dto.DiscountDTO.ManageResponse;
import com.shitcode.demo1.entity.Discount;
import com.shitcode.demo1.exception.model.EntityExistsException;
import com.shitcode.demo1.exception.model.EntityNotFoundException;
import com.shitcode.demo1.helper.DiscountDateTimeConverter;
import com.shitcode.demo1.helper.PaginationProvider;
import com.shitcode.demo1.mapper.DiscountMapper;
import com.shitcode.demo1.repository.DiscountRepository;
import com.shitcode.demo1.service.DiscountService;
import com.shitcode.demo1.utils.KeyLock;
import com.shitcode.demo1.utils.cache.DiscountCacheType;

import jakarta.annotation.PostConstruct;

@Service
@Transactional
@LogCollector
public class DiscountServiceImpl implements DiscountService {
    private final DiscountMapper discountMapper = DiscountMapper.INSTANCE;
    private final DiscountRepository discountRepository;
    private final DatabaseLock databaseLock;
    private final CacheManager cacheManager;

    public DiscountServiceImpl(DiscountRepository discountRepository, DatabaseLock databaseLock,
            CacheManager cacheManager) {
        this.discountRepository = discountRepository;
        this.databaseLock = databaseLock;
        this.cacheManager = cacheManager;
    }

    private Cache cache;

    @PostConstruct
    void setUp() {
        cacheManager.getCache(DiscountCacheType.Fields.EXPIRED_DISCOUNTS);
    }

    @Override
    @Cacheable(value = DiscountCacheType.Fields.DISCOUNT_ID, key = "#id")
    @Transactional(readOnly = true)
    public DiscountDetailsResponse findById(UUID id) {
        return discountMapper.toDiscountDetailsResponse(findDetailEntityById(id));
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = DiscountCacheType.Fields.DISCOUNTS_TITLE_EXPDATE, allEntries = true),
    })
    public ManageResponse create(ManageRequest request) {
        mustNotReturnEntityWhenFindingByTitle(request.getType().getFullTitle());
        Discount discount = discountMapper.toEntity(request);

        discount.setTitle(request.getType().getFullTitle());
        discount.setSalesPercentAmount(Optional.ofNullable(request.getSalesPercentAmount())
                .orElseGet(() -> request.getType().getSalesPercentAmount()));
        discount.setExpDate(DiscountDateTimeConverter.convert(request.getType()));

        Discount res = discountRepository.save(discount);

        cache.putIfAbsent(res.getId(), res.getExpDate().toString());

        return discountMapper.toManageResponse(res);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = DiscountCacheType.Fields.DISCOUNT_ID, key = "#id"),
            @CacheEvict(value = DiscountCacheType.Fields.DISCOUNTS_TITLE_EXPDATE, allEntries = true),
    })
    public ManageResponse update(ManageRequest request, UUID id) {
        Discount discount = findEntityById(id);

        discount.setTitle(request.getType().getFullTitle());
        discount.setType(request.getType());
        discount.setSalesPercentAmount(Optional.ofNullable(request.getSalesPercentAmount())
                .orElseGet(() -> request.getType().getSalesPercentAmount()));
        discount.setExpDate(DiscountDateTimeConverter.convert(request.getType()));

        ManageResponse response = databaseLock.doAndLock(KeyLock.DISCOUNT,
                () -> discountMapper.toManageResponse(discountRepository.save(discount)));

        cache.evictIfPresent(response.getId());
        cache.putIfAbsent(response.getId(), response.getExpDate().toString());

        return response;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = DiscountCacheType.Fields.DISCOUNT_ID, key = "#id"),
            @CacheEvict(value = DiscountCacheType.Fields.DISCOUNTS_TITLE_EXPDATE, allEntries = true),
    })
    public void delete(UUID id) {
        findEntityById(id);
        discountRepository.deleteById(id);
        cache.evictIfPresent(id);
    }

    @Override
    public void removeExpiredDiscountsFromProducts(UUID id) {
        discountRepository.removeExpiredDiscountsFromProducts(id);
    }

    @Override
    @Cacheable(value = DiscountCacheType.Fields.DISCOUNTS_TITLE_EXPDATE, key = "T(String).valueOf(#page) + '-' + T(String).valueOf(#size) + '-' + (#sort ?: 'default') + '-' + T(String).valueOf(#asc)")
    @Transactional(readOnly = true)
    public Page<ManageResponse> findByTitleAndExpDateBetween(int page, int size, @Nullable String sort, boolean asc,
            String title, OffsetDateTime startDate, OffsetDateTime endDate) {
        Pageable pageable = PaginationProvider.build(page, size, sort, asc);
        Page<Discount> res = discountRepository.findByTitleAndExpDateBetween(title, startDate, endDate, pageable);
        return res.map(discountMapper::toManageResponse);
    }

    @Override
    public Discount findEntityById(UUID id) {
        return discountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("{exception.entity-not-found.discount-id}"));
    }

    @Override
    public Discount findDetailEntityById(UUID id) {
        return discountRepository.findDetailDiscountById(id)
                .orElseThrow(() -> new EntityNotFoundException("{exception.entity-not-found.discount-id}"));
    }

    @Override
    public Discount findEntityByTitle(String title) {
        return discountRepository.findEntityByTitle(title)
                .orElseThrow(() -> new EntityNotFoundException("{exception.entity-not-found.discount-title}"));
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
