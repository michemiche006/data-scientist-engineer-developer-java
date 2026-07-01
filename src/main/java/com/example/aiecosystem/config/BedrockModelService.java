package com.example.aiecosystem.config;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class BedrockModelService {

    private final ChatModel chatModel;
    private final EmbeddingModel embeddingModel;
    private final AiWorkflowProperties properties;

    public BedrockModelService(ChatModel chatModel, EmbeddingModel embeddingModel, AiWorkflowProperties properties) {
        this.chatModel = chatModel;
        this.embeddingModel = embeddingModel;
        this.properties = properties;
    }

    public List<Double> embed(String text) {
        try {
            Embedding embedding = embeddingModel.embed(text).content();
            return toDoubleList(embedding.vector());
        } catch (RuntimeException ex) {
            System.out.printf("Bedrock embedding failed, using local fallback: %s%n", ex.getMessage());
            return deterministicEmbedding(text, properties.getVector().getDimension());
        }
    }

    public String chat(String prompt) {
        try {
            return chatModel.chat(prompt);
        } catch (RuntimeException ex) {
            System.out.printf("Bedrock chat failed, using local fallback: %s%n", ex.getMessage());
            return "I could not reach Bedrock with the configured API key, but I found relevant local context. "
                    + "Review the sources returned with this answer and set AWS_BEARER_TOKEN_BEDROCK to enable model responses.";
        }
    }

    private static List<Double> toDoubleList(float[] vector) {
        List<Double> result = new ArrayList<>(vector.length);
        for (float value : vector) {
            result.add((double) value);
        }
        return result;
    }

    private static List<Double> deterministicEmbedding(String text, int dimensions) {
        Random random = new Random(seed(text));
        List<Double> vector = new ArrayList<>(dimensions);
        for (int i = 0; i < dimensions; i++) {
            vector.add(random.nextDouble(-0.5, 0.5));
        }
        return vector;
    }

    private static long seed(String text) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(text.getBytes(StandardCharsets.UTF_8));
            long seed = 0;
            for (int i = 0; i < Long.BYTES; i++) {
                seed = (seed << 8) | (digest[i] & 0xffL);
            }
            return seed;
        } catch (NoSuchAlgorithmException ex) {
            return text.hashCode();
        }
    }
}
