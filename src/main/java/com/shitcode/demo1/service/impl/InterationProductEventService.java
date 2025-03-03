package com.shitcode.demo1.service.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.shitcode.demo1.dto.ProductInteractionDTO.Request;
import com.shitcode.demo1.repository.ProductInteractionRepository;
import com.shitcode.demo1.service.InterationEventService;
import com.shitcode.demo1.service.ProductService;

import jakarta.transaction.Transactional;

@Service
@Transactional
@Qualifier("productEvent")
public class InterationProductEventService implements InterationEventService {

    private final ProductInteractionRepository interactionRepository;
    private final ProductService productService;

    public InterationProductEventService(ProductInteractionRepository interactionRepository,
            ProductService productService) {
        this.interactionRepository = interactionRepository;
        this.productService = productService;
    }

    @Override
    public boolean recordNewEvent(Request interation) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'recordNewEvent'");
    }

}
