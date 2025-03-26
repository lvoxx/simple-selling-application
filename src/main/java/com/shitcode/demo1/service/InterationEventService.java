package com.shitcode.demo1.service;

import java.time.Instant;
import java.util.List;

import com.shitcode.demo1.dto.ProductInteractionDTO;
import com.shitcode.demo1.dto.ProductInteractionDTO.PageResponse;

public interface InterationEventService {
    boolean recordNewEvent(ProductInteractionDTO.Request interation);

    List<PageResponse> findRecordsWithTimeBetween(Integer page, Integer size, Instant startTime,
            Instant endTime);
}