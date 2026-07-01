package com.example.aiecosystem;

import com.example.aiecosystem.config.BedrockApiKeyNotice;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AiEcosystemApplication {

    public static void main(String[] args) {
        BedrockApiKeyNotice.printIfMissing();
        SpringApplication.run(AiEcosystemApplication.class, args);
    }
}
