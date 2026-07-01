package com.example.aiecosystem.model;

import java.time.Instant;

public record ContentArticle(
        String url,
        String title,
        String content,
        int wordCount,
        Instant fetchedAt
) {
}
