package com.example.aiecosystem.config;

import java.nio.file.Path;

public final class ProjectPaths {

    public static final Path ROOT = Path.of("").toAbsolutePath().normalize();
    public static final Path EXERCISE_2 = ROOT.resolve("exercise-2-data-engineering");
    public static final Path EXERCISE_3 = ROOT.resolve("exercise-3-app-development");
    public static final Path DATA_SCIENCE_OUTPUT = EXERCISE_2.resolve("data-science-output.json");
    public static final Path PIPELINE_REPORT = EXERCISE_2.resolve("pipeline-report.json");
    public static final Path CHROMA_FALLBACK_DIR = EXERCISE_2.resolve("chroma_db");
    public static final Path VECTOR_DB_CONFIG = EXERCISE_3.resolve("vector-db-config.json");

    private ProjectPaths() {
    }
}
