package com.example.aiecosystem.datascience;

import java.util.Collection;
import java.util.DoubleSummaryStatistics;

public final class DataAnalysisTools {

    public static double mean(Collection<? extends Number> values) {
        return values.stream()
                .mapToDouble(Number::doubleValue)
                .average()
                .orElse(0);
    }

    public static DoubleSummaryStatistics describe(Collection<? extends Number> values) {
        return values.stream()
                .mapToDouble(Number::doubleValue)
                .summaryStatistics();
    }

    private DataAnalysisTools() {
    }
}
