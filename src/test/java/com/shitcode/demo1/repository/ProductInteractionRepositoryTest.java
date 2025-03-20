package com.shitcode.demo1.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.test.context.ActiveProfiles;

import com.shitcode.demo1.testcontainer.AbstractRepositoryTest;

@AutoConfigureTestDatabase(replace = Replace.NONE) // Dont load String datasource autoconfig
@ActiveProfiles("test")
@DisplayName("Discount Repository Tests")
@Tags({
        @Tag("Reporitory"), @Tag("No Mock")
})
public class ProductInteractionRepositoryTest extends AbstractRepositoryTest {
    @Autowired
    ProductInteractionRepository productInteractionRepository;
}
