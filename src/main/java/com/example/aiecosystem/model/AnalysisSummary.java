package com.example.aiecosystem.model;

import java.time.Instant;
import java.util.List;

public record AnalysisSummary(
        int totalArticles,
        int avgWordCount,
        double avgQualityScore,
        List<String> topTopics,
        String recommendation,
        Instant generatedAt
) {
}
