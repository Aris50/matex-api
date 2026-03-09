package com.matex.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        // public
                        .requestMatchers("/api/v1/auth/login").permitAll()
                        .requestMatchers("/api/v1/health").permitAll()

                        // swagger (public during dev)
                        .requestMatchers(
                                "/swagger/**", "/swagger-ui/**", "/swagger-ui.html",
                                "/api-docs/**", "/api-docs.yaml", "/v3/api-docs/**"
                        ).permitAll()

                        // static resources
                        .requestMatchers(HttpMethod.GET, "/", "/index.html", "/student.html", "/teacher.html", "/*.js", "/*.css").permitAll()

                        // owner-only
                        .requestMatchers("/api/v1/owner/**").hasRole("OWNER")

                        // teacher endpoints (OWNER + TEACHER)
                        .requestMatchers("/api/v1/teacher/**").hasAnyRole("OWNER", "TEACHER")

                        // student endpoints
                        .requestMatchers("/api/v1/student/**").hasAnyRole("OWNER", "TEACHER", "STUDENT")

                        // everything else requires authentication
                        .anyRequest().authenticated()
                )

                .httpBasic(basic -> basic.disable())
                .formLogin(form -> form.disable())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}