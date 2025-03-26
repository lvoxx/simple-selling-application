package com.shitcode.demo1.service.impl;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.shitcode.demo1.component.IpAddressResolver;
import com.shitcode.demo1.dto.ProductInteractionDTO.PageResponse;
import com.shitcode.demo1.dto.ProductInteractionDTO.Request;
import com.shitcode.demo1.entity.ProductInteraction;
import com.shitcode.demo1.repository.ProductInteractionRepository;
import com.shitcode.demo1.service.InterationEventService;
import com.shitcode.demo1.service.Ip2LocationService;
import com.shitcode.demo1.utils.LogPrinter;
import com.shitcode.demo1.utils.cache.ProductInterationCacheType;

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
                .productId(interation.getProductId())
                .username(AuthServiceImpl.getAuthenticatedUsername())
                .locateAt(ip2LocationService
                        .getLocation(ipAddressResolver.getClientIp(AuthServiceImpl.getHttpServletRequest())).toString())
                .onTime(LocalDateTime.now())
                .build();
        try {
            interactionRepository.saveAndFlush(productInteraction);
        } catch (Exception e) {
            LogPrinter.printLog(LogPrinter.Type.ERROR, LogPrinter.Flag.SERVICE_FLAG, e.getMessage());
            return false;
        }
        return true;
    }

    @Override
    @Cacheable(value = ProductInterationCacheType.Fields.TIME_BETWEEN, key = "T(String).valueOf(#page) + '-' + T(String).valueOf(#size) + '-' + T(String).valueOf(#startTime) + '-' + T(String).valueOf(#endTime)")
    public List<PageResponse> findRecordsWithTimeBetween(Integer page, Integer size, LocalDateTime startTime,
            LocalDateTime endTime) {
        List<Object[]> res = interactionRepository.findPageByTimeBetween(page - 1, size, startTime, endTime);
        List<PageResponse> pageResponse = new LinkedList<>();
        res.stream().forEach(r -> {
            pageResponse.add(PageResponse.builder()
                    .productId((Long) r[0])
                    .productName((String) r[1])
                    .categoryName((String) r[2])
                    .locateAt((String) r[3])
                    .onTime((LocalDateTime) r[4])
                    .build());
        });
        return pageResponse;
    }

}
