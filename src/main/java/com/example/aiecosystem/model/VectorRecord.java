package com.example.aiecosystem.model;

import java.util.List;

public record VectorRecord(
        String id,
        List<Double> vector,
        String document,
        ChunkMetadata metadata
) {
}
