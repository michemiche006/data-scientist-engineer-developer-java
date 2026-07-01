package com.example.aiecosystem.model;

import java.time.Instant;
import java.util.List;

public record DataScienceExport(
        AnalysisSummary analysis,
        List<ContentAnalysisResult> contentData,
        Instant exportedAt,
        String nextStep
) {
}
