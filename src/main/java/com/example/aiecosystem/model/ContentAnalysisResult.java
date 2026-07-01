package com.example.aiecosystem.model;

import java.time.Instant;
import java.util.List;

public record ContentAnalysisResult(
        String url,
        String title,
        String content,
        int wordCount,
        int uniqueWords,
        List<String> topKeywords,
        double readabilityScore,
        double qualityScore,
        Instant analyzedAt
) {
}
