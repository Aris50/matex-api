package com.matex.api.config;

import com.matex.api.repo.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Runs once at startup.
 * If the OWNER account's stored hash doesn't match the configured password,
 * it re-hashes it properly so login works.
 */
@Component
public class OwnerAccountInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(OwnerAccountInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${OWNER_DEFAULT_PASSWORD:owner123}")
    private String ownerDefaultPassword;

    public OwnerAccountInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        userRepository.findByEmail("owner@matex.app").ifPresent(owner -> {
            if (!passwordEncoder.matches(ownerDefaultPassword, owner.getPasswordHash())) {
                String properHash = passwordEncoder.encode(ownerDefaultPassword);
                owner.setPasswordHash(properHash);
                userRepository.save(owner);
                log.warn("OWNER password was re-hashed. Change the default password!");
            }
        });
    }
}

