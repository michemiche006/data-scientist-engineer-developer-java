package com.example.aiecosystem.appdev;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public final class ChatDtos {

    public record ChatRequest(String message, String conversationId) {
    }

    public record Source(String title, String url, double similarity) {
    }

    public record ChatResult(String response, String conversationId, Map<String, Object> metadata, List<Source> sources) {
    }

    public record ConversationTurn(Instant timestamp, String query, String response, int contextUsed, List<String> sources) {
    }

    private ChatDtos() {
    }
}
