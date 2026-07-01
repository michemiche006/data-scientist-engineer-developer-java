package com.example.aiecosystem.config;

public final class BedrockApiKeyNotice {

    public static void printIfMissing() {
        if (EnvironmentConfig.get("AWS_BEARER_TOKEN_BEDROCK").isEmpty()) {
            System.out.println("AWS_BEARER_TOKEN_BEDROCK is not set. Bedrock calls will use local demo fallbacks.");
            System.out.println("Set AWS_BEARER_TOKEN_BEDROCK to use a Bedrock API key without AWS access-key secrets.");
        }
    }

    private BedrockApiKeyNotice() {
    }
}
