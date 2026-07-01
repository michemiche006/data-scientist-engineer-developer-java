package com.example.aiecosystem.appdev;

import com.example.aiecosystem.config.BedrockModelService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class RagChatAgent {

    private final BedrockModelService modelService;
    private final LocalVectorStore vectorStore;
    private final Map<String, List<ChatDtos.ConversationTurn>> conversationHistory = new LinkedHashMap<>();

    public RagChatAgent(BedrockModelService modelService, LocalVectorStore vectorStore) {
        this.modelService = modelService;
        this.vectorStore = vectorStore;
    }

    public boolean isReady() {
        return vectorStore.isReady();
    }

    public int vectorCount() {
        return vectorStore.count();
    }

    public ChatDtos.ChatResult chat(String query, String conversationId) throws Exception {
        long start = System.currentTimeMillis();
        String normalizedQuery = query.trim();
        String activeConversationId = conversationId == null || conversationId.isBlank()
                ? UUID.randomUUID().toString()
                : conversationId;

        List<Double> queryEmbedding = modelService.embed(normalizedQuery.toLowerCase());
        List<RagSearchResult> searchResults = vectorStore.search(queryEmbedding, 3);
        String prompt = buildRagPrompt(normalizedQuery, activeConversationId, searchResults);
        String response = modelService.chat(prompt);

        List<ChatDtos.Source> sources = searchResults.stream()
                .map(result -> new ChatDtos.Source(
                        result.record().metadata().title(),
                        result.record().metadata().url(),
                        result.similarity()
                ))
                .toList();

        updateMemory(activeConversationId, normalizedQuery, response, sources);

        Map<String, Object> metadata = Map.of(
                "searchResults", searchResults.size(),
                "responseTime", System.currentTimeMillis() - start,
                "tokensUsed", 0
        );
        return new ChatDtos.ChatResult(response, activeConversationId, metadata, sources);
    }

    public List<ChatDtos.ConversationTurn> getConversationHistory(String conversationId) {
        return conversationHistory.getOrDefault(conversationId, List.of());
    }

    private String buildRagPrompt(String query, String conversationId, List<RagSearchResult> results) {
        StringBuilder context = new StringBuilder();
        for (RagSearchResult result : results) {
            context.append("Source: ")
                    .append(result.record().metadata().title())
                    .append("\nURL: ")
                    .append(result.record().metadata().url())
                    .append("\nContent: ")
                    .append(result.record().document())
                    .append("\n\n");
        }

        return """
                You are a helpful AI assistant specializing in technology and machine learning topics.

                Use the following context from relevant documents to answer the user's question.
                If the context does not contain relevant information, say so clearly.

                Conversation ID: %s

                CONTEXT:
                %s

                USER QUESTION: %s

                Please provide a helpful, accurate response based on the context above. Include references to specific sources when possible.
                """.formatted(conversationId, context, query);
    }

    private void updateMemory(String conversationId, String query, String response, List<ChatDtos.Source> sources) {
        List<ChatDtos.ConversationTurn> history = conversationHistory.computeIfAbsent(conversationId, ignored -> new ArrayList<>());
        history.add(new ChatDtos.ConversationTurn(
                Instant.now(),
                query,
                response,
                sources.size(),
                sources.stream().map(ChatDtos.Source::title).toList()
        ));
        if (history.size() > 10) {
            history.remove(0);
        }
    }
}
