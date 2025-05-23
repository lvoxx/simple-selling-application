package com.shitcode.demo1.dto;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractAuditableEntity {
    protected Instant createdAt = Instant.now();
    protected Instant updatedAt = Instant.now();
}
