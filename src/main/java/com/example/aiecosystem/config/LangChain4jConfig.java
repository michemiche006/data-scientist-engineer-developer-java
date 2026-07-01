package com.example.aiecosystem.config;

import dev.langchain4j.model.bedrock.BedrockChatModel;
import dev.langchain4j.model.bedrock.BedrockChatRequestParameters;
import dev.langchain4j.model.bedrock.BedrockTitanEmbeddingModel;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.http.auth.spi.scheme.AuthSchemeOption;
import software.amazon.awssdk.identity.spi.IdentityProvider;
import software.amazon.awssdk.identity.spi.ResolveIdentityRequest;
import software.amazon.awssdk.identity.spi.TokenIdentity;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClientBuilder;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Configuration
@EnableConfigurationProperties(AiWorkflowProperties.class)
public class LangChain4jConfig {

    @Bean
    BedrockRuntimeClient bedrockRuntimeClient(AiWorkflowProperties properties) {
        BedrockRuntimeClientBuilder builder = BedrockRuntimeClient.builder()
                .region(Region.of(properties.getBedrock().getRegion()));
        EnvironmentConfig.get("AWS_BEARER_TOKEN_BEDROCK")
                .ifPresent(apiKey -> {
                    builder.tokenProvider(new StaticBedrockTokenProvider(apiKey));
                    // Force Bearer Token auth scheme — prevents the SDK from trying SigV4 first
                    // which would fail without AWS_ACCESS_KEY_ID / AWS_SECRET_ACCESS_KEY
                    builder.authSchemeProvider(params -> List.of(
                            AuthSchemeOption.builder().schemeId("smithy.api#httpBearerAuth").build()
                    ));
                });
        return builder.build();
    }

    @Bean
    ChatModel chatModel(AiWorkflowProperties properties, BedrockRuntimeClient bedrockRuntimeClient) {
        BedrockChatRequestParameters parameters = BedrockChatRequestParameters.builder()
                .temperature(0.3)
                .maxOutputTokens(800)
                .build();

        return BedrockChatModel.builder()
                .client(bedrockRuntimeClient)
                .modelId(properties.getBedrock().getChatModelId())
                .defaultRequestParameters(parameters)
                .build();
    }

    @Bean
    EmbeddingModel embeddingModel(AiWorkflowProperties properties, BedrockRuntimeClient bedrockRuntimeClient) {
        return BedrockTitanEmbeddingModel.builder()
                .client(bedrockRuntimeClient)
                .model(properties.getBedrock().getEmbeddingsModelId())
                .build();
    }

    private record StaticBedrockTokenProvider(String token) implements IdentityProvider<TokenIdentity> {

        @Override
        public Class<TokenIdentity> identityType() {
            return TokenIdentity.class;
        }

        @Override
        public CompletableFuture<? extends TokenIdentity> resolveIdentity(ResolveIdentityRequest request) {
            return CompletableFuture.completedFuture(TokenIdentity.create(token));
        }
    }
}
