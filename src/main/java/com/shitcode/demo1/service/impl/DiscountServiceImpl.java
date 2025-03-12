package com.shitcode.demo1.service.impl;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import com.shitcode.demo1.annotation.logging.LogCollector;
import com.shitcode.demo1.component.DatabaseLock;
import com.shitcode.demo1.dto.DiscountDTO.ManageRequest;
import com.shitcode.demo1.dto.DiscountDTO.ManageResponse;
import com.shitcode.demo1.entity.Discount;
import com.shitcode.demo1.mapper.DiscountMapper;
import com.shitcode.demo1.repository.DiscountRepository;
import com.shitcode.demo1.service.DiscountService;

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
    public ManageResponse create(ManageRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'create'");
    }

    @Override
    public ManageResponse update(ManageRequest request, Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public void delete(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public void removeExpiredDiscountsFromProducts() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'removeExpiredDiscountsFromProducts'");
    }

    @Override
    public List<ManageResponse> findByTitleAndExpDateBetween(int page, int size, @Nullable String sort, boolean asc,
            String title) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByTitleAndExpDateBetween'");
    }

    @Override
    public List<Discount> findEntitiesByTitleAndExpDateBetween(String title, OffsetDateTime startDate,
            OffsetDateTime endDate) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findEntitiesByTitleAndExpDateBetween'");
    }

    @Override
    public List<Discount> findEntitiesByTitle(String title) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findEntitiesByTitle'");
    }

    @Override
    public List<Discount> findEntitiesByExpDateBetween(OffsetDateTime startDate, OffsetDateTime endDate) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findEntitiesByExpDateBetween'");
    }
}
