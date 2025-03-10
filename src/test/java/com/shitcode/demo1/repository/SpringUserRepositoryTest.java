package com.shitcode.demo1.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.test.context.ActiveProfiles;

import com.shitcode.demo1.entity.SpringUser;
import com.shitcode.demo1.testcontainer.AbstractRepositoryTest;

@AutoConfigureTestDatabase(replace = Replace.NONE) // Dont load String datasource autoconfig
@ActiveProfiles("test")
@DisplayName("SpringUser Repository Tests")
@Tags({
        @Tag("Reporitory"), @Tag("No Mock")
})
public class SpringUserRepositoryTest extends AbstractRepositoryTest {
    @Autowired
    SpringUserRepository customerRepository;

    @BeforeEach
    void setUp() {
        List<SpringUser> customers = List.of(
                SpringUser.builder()
                        .email("john.doe@example.com")
                        .password("securePassword123")
                        .firstName("John")
                        .lastName("Doe")
                        .phoneNumber("+84-09991")
                        .build(),
                SpringUser.builder()
                        .email("jane.smith@example.com")
                        .password("anotherPassword456")
                        .firstName("Jane")
                        .lastName("Smith")
                        .phoneNumber("+84-09992")
                        .build(),
                SpringUser.builder()
                        .email("bob.johnson@example.com")
                        .password("password789")
                        .firstName("Bob")
                        .lastName("Johnson")
                        .phoneNumber("+84-09993")
                        .build());
        customerRepository.saveAll(customers);
    }

    @AfterEach
    void tearDown() {
        customerRepository.deleteAll();
    }

    @Test
    @Order(1)
    @DisplayName("Should return 3 customers")
    void shouldReturn3Customers() {
        // When
        List<SpringUser> customers = customerRepository.findAll();

        // Then
        assertThat(customers)
                .isNotEmpty()
                .hasSize(3);
    }

    @Test
    @Order(1)
    @DisplayName("Should Return Matching Credentials And Personal Information When Finding By Email")
    void shouldReturnMatchingCredentialsAndPersonalInformation_whenFindingById() {
        // Given
        Long exitingId = customerRepository.findAll().stream()
                .filter(c -> "jane.smith@example.com".equals(c.getEmail()))
                .findFirst()
                .map(SpringUser::getId)
                .orElseThrow(() -> new IllegalStateException("SpringUser not found"));

        // When
        Optional<SpringUser> customer = customerRepository.findById(exitingId);

        // Then
        assertThat(customer).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getEmail()).isEqualTo("jane.smith@example.com");
            assertThat(c.getPassword()).isEqualTo("anotherPassword456");
            assertThat(c.getFirstName()).isEqualTo("Jane");
            assertThat(c.getLastName()).isEqualTo("Smith");
        });
    }

    @Test
    @Order(1)
    @DisplayName("Should Return Matching Credentials And Personal Information")
    void shouldReturnMatchingCredentialsAndPersonalInformation_whenFindingByEmail() {
        // When
        Optional<SpringUser> customer = customerRepository.findByEmail("john.doe@example.com");

        // Then
        assertThat(customer).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getEmail()).isEqualTo("john.doe@example.com");
            assertThat(c.getPassword()).isEqualTo("securePassword123");
            assertThat(c.getFirstName()).isEqualTo("John");
            assertThat(c.getLastName()).isEqualTo("Doe");
        });
    }

    @Test
    @Order(2)
    @DisplayName("Should Return Empty When Finding By Non-Existing Email")
    void shouldReturnEmptyWhenFindingByNonExistingEmail() {
        // When
        Optional<SpringUser> customer = customerRepository.findByEmail("dummy@email.vien");

        // Then
        assertThat(customer).isNotPresent();
    }

    @Test
    @Order(2)
    @DisplayName("Should Return Empty When Finding By Non-Existing Id")
    void shouldReturnEmptyWhenFindingByNonExistingId() {
        // When
        Optional<SpringUser> customer = customerRepository.findById(-1L);

        // Then
        assertThat(customer).isNotPresent();
    }

    @Test
    @Order(3)
    @DisplayName("Should Update SpringUser Data And Return New SpringUser Data")
    void shouldUpdateCustomerData_andReturnNewCustomerData() {
        // Given
        SpringUser oldCustomer = customerRepository.findByEmail("bob.johnson@example.com").get();
        SpringUser newCustomerData = SpringUser.builder()
                .email("new.email@gmail.com")
                .password("NewPassword123@#")
                .firstName("New FirstName")
                .lastName("New LastName")
                .build();
        // When
        oldCustomer.setEmail(newCustomerData.getEmail());
        oldCustomer.setPassword(newCustomerData.getPassword());
        oldCustomer.setFirstName(newCustomerData.getFirstName());
        oldCustomer.setLastName(newCustomerData.getLastName());

        Optional<SpringUser> updatedCustomerData = Optional.ofNullable(customerRepository.save(oldCustomer));
        // Then
        assertThat(updatedCustomerData).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getEmail()).isEqualTo(newCustomerData.getEmail());
            assertThat(c.getPassword()).isEqualTo(newCustomerData.getPassword());
            assertThat(c.getFirstName()).isEqualTo(newCustomerData.getFirstName());
            assertThat(c.getLastName()).isEqualTo(newCustomerData.getLastName());
        });
    }

}
