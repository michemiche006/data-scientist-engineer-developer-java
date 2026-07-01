package com.example.aiecosystem.model;

import java.time.Instant;
import java.util.List;

public record ChunkMetadata(
        String title,
        String url,
        int chunkIndex,
        int totalChunks,
        List<String> keywords,
        double qualityScore,
        int wordCount,
        Instant processedAt
) {
}
