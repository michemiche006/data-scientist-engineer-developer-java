package com.example.aiecosystem.model;

public record ContentChunk(
        String id,
        String content,
        ChunkMetadata metadata
) {
}
