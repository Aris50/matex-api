package com.matex.api.controller.owner;

import com.matex.api.domain.User;
import com.matex.api.domain.enums.UserRole;
import com.matex.api.repo.UserRepository;
import com.matex.api.web.dto.AccountResponse;
import com.matex.api.web.dto.CreateAccountRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/owner/accounts")
public class OwnerAccountController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final Set<String> ALLOWED_ROLES = Set.of("STUDENT", "TEACHER");

    public OwnerAccountController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse createAccount(@RequestBody CreateAccountRequest req) {
        if (req.email() == null || req.email().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "email is required");
        }
        if (req.password() == null || req.password().length() < 6) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "password must be at least 6 characters");
        }
        if (req.fullName() == null || req.fullName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "fullName is required");
        }
        if (req.role() == null || !ALLOWED_ROLES.contains(req.role().toUpperCase())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "role must be STUDENT or TEACHER");
        }

        if (userRepository.findByEmail(req.email()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "email already exists");
        }

        User user = new User();
        user.setEmail(req.email().trim().toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(req.password()));
        user.setFullName(req.fullName().trim());
        user.setRole(UserRole.valueOf(req.role().toUpperCase()));

        User saved = userRepository.save(user);

        return new AccountResponse(
                saved.getId(),
                saved.getEmail(),
                saved.getFullName(),
                saved.getRole().name(),
                saved.getCreatedAt()
        );
    }

    @GetMapping
    public List<AccountResponse> listAccounts() {
        return userRepository.findAll().stream()
                .map(u -> new AccountResponse(
                        u.getId(),
                        u.getEmail(),
                        u.getFullName(),
                        u.getRole().name(),
                        u.getCreatedAt()
                ))
                .toList();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccount(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "account not found"));

        if (user.getRole() == UserRole.OWNER) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "cannot delete the owner account");
        }

        userRepository.delete(user);
    }
}

