package com.example.aiecosystem.model;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record VectorCollection(
        String name,
        Map<String, String> metadata,
        List<VectorRecord> embeddings,
        Instant createdAt,
        int totalVectors
) {
}
