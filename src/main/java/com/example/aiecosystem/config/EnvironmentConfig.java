package com.example.aiecosystem.config;

import io.github.cdimascio.dotenv.Dotenv;

import java.util.Optional;

public final class EnvironmentConfig {

    private static final Dotenv DOTENV = Dotenv.configure()
            .ignoreIfMalformed()
            .ignoreIfMissing()
            .load();

    public static Optional<String> get(String key) {
        String environmentValue = System.getenv(key);
        if (environmentValue != null && !environmentValue.isBlank()) {
            return Optional.of(environmentValue);
        }

        String dotenvValue = DOTENV.get(key);
        if (dotenvValue != null && !dotenvValue.isBlank()) {
            return Optional.of(dotenvValue);
        }

        return Optional.empty();
    }

    private EnvironmentConfig() {
    }
}
