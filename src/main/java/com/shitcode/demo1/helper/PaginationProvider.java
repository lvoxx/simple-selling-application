package com.shitcode.demo1.helper;

import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public abstract class PaginationProvider {
    public static Pageable build(@DefaultValue("1") int page, @DefaultValue("30") int size,
            @DefaultValue("id") String sort, @DefaultValue("false") boolean asc) {
        page = Math.max(0, page - 1);
        Sort sortBy = asc ? Sort.by(sort).ascending() : Sort.by(sort).descending();
        return PageRequest.of(page, size, sortBy);
    }
}
