package com.shitcode.demo1.init;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.shitcode.demo1.entity.SpringUser;
import com.shitcode.demo1.properties.AccountsConfigData;
import com.shitcode.demo1.repository.SpringUserRepository;

@Component
@Profile("!test")
public class ApplicationInitializer implements CommandLineRunner {

    @Lazy
    private final AccountsConfigData accountsConfigData;
    @Lazy
    private final SpringUserRepository userRepository;
    @Lazy
    private final BCryptPasswordEncoder passwordEncoder;

    public ApplicationInitializer(AccountsConfigData accountsConfigData, SpringUserRepository userRepository,
            BCryptPasswordEncoder passwordEncoder) {
        this.accountsConfigData = accountsConfigData;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        AccountsConfigData.ApplicationAccount account = accountsConfigData.getRoot();
        SpringUser user = SpringUser.builder()
                .email(account.getEmail())
                .password(passwordEncoder.encode(account.getPassword()))
                .firstName(account.getFirstName())
                .lastName(account.getLastName())
                .phoneNumber(account.getPhone())
                .locked(account.isLocked())
                .enabled(account.isEnabled())
                .points(account.getPoints())
                .roles(List.of(account.getRoles()))
                .build();
        userRepository.save(user);
    }

}
