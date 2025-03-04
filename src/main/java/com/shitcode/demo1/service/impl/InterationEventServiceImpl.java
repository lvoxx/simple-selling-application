package com.shitcode.demo1.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.shitcode.demo1.component.IpAddressResolver;
import com.shitcode.demo1.dto.ProductInteractionDTO.Request;
import com.shitcode.demo1.entity.ProductInteraction;
import com.shitcode.demo1.repository.ProductInteractionRepository;
import com.shitcode.demo1.service.InterationEventService;
import com.shitcode.demo1.service.Ip2LocationService;
import com.shitcode.demo1.utils.LogPrinter;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class InterationEventServiceImpl implements InterationEventService {

    private final ProductInteractionRepository interactionRepository;
    private final Ip2LocationService ip2LocationService;
    private final IpAddressResolver ipAddressResolver;

    public InterationEventServiceImpl(ProductInteractionRepository interactionRepository,
            Ip2LocationService ip2LocationService, IpAddressResolver ipAddressResolver) {
        this.interactionRepository = interactionRepository;
        this.ip2LocationService = ip2LocationService;
        this.ipAddressResolver = ipAddressResolver;
    }

    @Override
    public boolean recordNewEvent(Request interation) {
        ProductInteraction productInteraction = ProductInteraction.builder()
                .event(interation.getEvent())
                .productName(interation.getProductName())
                .categoryName(interation.getCategoryName())
                .by(AuthServiceImpl.getAuthenticatedUsername())
                .at(ip2LocationService
                        .getLocation(ipAddressResolver.getClientIp(AuthServiceImpl.getHttpServletRequest())).toString())
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
