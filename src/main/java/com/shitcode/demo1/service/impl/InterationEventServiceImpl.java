package com.shitcode.demo1.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.shitcode.demo1.dto.ProductInteractionDTO.Request;
import com.shitcode.demo1.entity.ProductInteraction;
import com.shitcode.demo1.repository.ProductInteractionRepository;
import com.shitcode.demo1.service.InterationEventService;
import com.shitcode.demo1.utils.LogPrinter;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class InterationEventServiceImpl implements InterationEventService {

    private final ProductInteractionRepository interactionRepository;

    public InterationEventServiceImpl(ProductInteractionRepository interactionRepository) {
        this.interactionRepository = interactionRepository;
    }

    @Override
    public boolean recordNewEvent(Request interation) {
        ProductInteraction productInteraction = ProductInteraction.builder()
                .event(interation.getEvent())
                .productName(interation.getProductName())
                .categoryName(interation.getCategoryName())
                .by(AuthServiceImpl.getAuthenticatedUsername())
                .at(null)
                .on(LocalDateTime.now())
                .build();
        try {
            interactionRepository.saveAndFlush(productInteraction);
        } catch (Exception e) {
            LogPrinter.printLog(LogPrinter.Type.ERROR, LogPrinter.Flag.SERVICE_FLAG, e.getMessage());
            return false;
        }
        return true;
    }

}
