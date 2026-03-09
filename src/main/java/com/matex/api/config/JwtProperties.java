package com.matex.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "matex.jwt")
public record JwtProperties(
        String secret,
        long expirationMs
) {}

