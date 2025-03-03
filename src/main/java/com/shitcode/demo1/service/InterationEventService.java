package com.shitcode.demo1.service;

import com.shitcode.demo1.dto.ProductInteractionDTO;

public interface InterationEventService {
    boolean recordNewEvent(ProductInteractionDTO.Request interation);
}