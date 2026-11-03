package com.mygym.app.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {

    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    public String hash(String rawPassword) {
        if (rawPassword == null || rawPassword.isBlank()) {
            return null;
        }
        return isHashed(rawPassword) ? rawPassword : encoder.encode(rawPassword);
    }

    public String hashOrKeep(String newPassword, String currentHash) {
        return (newPassword == null || newPassword.isBlank()) ? currentHash : hash(newPassword);
    }

    public boolean matches(String rawPassword, String storedPassword) {
        if (rawPassword == null || rawPassword.isEmpty() || storedPassword == null || storedPassword.isEmpty()) {
            return false;
        }
        return isHashed(storedPassword) ? encoder.matches(rawPassword, storedPassword) : storedPassword.equals(rawPassword);
    }

    public boolean needsUpgrade(String storedPassword) {
        return storedPassword != null && !isHashed(storedPassword);
    }

    private boolean isHashed(String value) {
        return value.startsWith("$2a$") || value.startsWith("$2b$") || value.startsWith("$2y$");
    }
}
