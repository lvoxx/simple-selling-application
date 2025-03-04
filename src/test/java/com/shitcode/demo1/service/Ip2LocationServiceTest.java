package com.shitcode.demo1.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.shitcode.demo1.config.Ip2LocationConfig;
import com.shitcode.demo1.service.impl.Ip2LocationServiceImpl;

import lombok.SneakyThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { Ip2LocationConfig.class, Ip2LocationServiceImpl.class })
@DisplayName("Ip2Location Service Tests")
@Tags({
        @Tag("Service"), @Tag("No Mock")
})
public class Ip2LocationServiceTest {
    @Autowired
    Ip2LocationService ip2LocationService;

    Map<String, String> ipAddresses = new LinkedHashMap<>();

    @BeforeEach
    void setUp() {
        ipAddresses.put("211.206.4.190", "Korea (the Republic of)");
        ipAddresses.put("106.13.86.93", "China");
        ipAddresses.put("159.64.75.43", "United States of America");
        ipAddresses.put("90.30.170.126", "France");
        ipAddresses.put("220.106.189.185", "Japan");
    }

    @AfterEach
    void tearDown() {
        ipAddresses = null;
    }

    @Test
    @DisplayName("Should return city and country when finding by IP address")
    @SneakyThrows
    void shouldReturnCityAndCountryWhenFindingByIpAddress() {
        ipAddresses.forEach((ip, loc) -> {
            assertThat(ip2LocationService.getLocation(ip)).isNotNull().satisfies(l -> {
                assertThat(l.getCountryLong()).isEqualTo(loc);
            });
        });
    }
}
