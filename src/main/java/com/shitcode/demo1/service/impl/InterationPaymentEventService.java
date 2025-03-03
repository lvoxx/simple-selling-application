package com.shitcode.demo1.service.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.shitcode.demo1.dto.ProductInteractionDTO.Request;
import com.shitcode.demo1.repository.ProductInteractionRepository;
import com.shitcode.demo1.service.InterationEventService;

import jakarta.transaction.Transactional;

@Service
@Transactional
@Qualifier("paymentEvent")
public class InterationPaymentEventService implements InterationEventService {

    private final ProductInteractionRepository interactionRepository;

    public InterationPaymentEventService(ProductInteractionRepository interactionRepository) {
        this.interactionRepository = interactionRepository;
    }

    @Override
    public boolean recordNewEvent(Request interation) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'recordNewEvent'");
    }

}
