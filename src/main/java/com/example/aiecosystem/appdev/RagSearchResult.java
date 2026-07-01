package com.example.aiecosystem.appdev;

import com.example.aiecosystem.model.VectorRecord;

public record RagSearchResult(
        VectorRecord record,
        double similarity
) {
}
