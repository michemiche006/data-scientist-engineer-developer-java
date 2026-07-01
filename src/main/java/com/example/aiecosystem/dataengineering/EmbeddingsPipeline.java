package com.example.aiecosystem.dataengineering;

import com.example.aiecosystem.config.AiWorkflowProperties;
import com.example.aiecosystem.config.BedrockModelService;
import com.example.aiecosystem.config.JacksonConfig;
import com.example.aiecosystem.config.ProjectPaths;
import com.example.aiecosystem.model.ChunkMetadata;
import com.example.aiecosystem.model.ContentAnalysisResult;
import com.example.aiecosystem.model.ContentChunk;
import com.example.aiecosystem.model.DataScienceExport;
import com.example.aiecosystem.model.VectorCollection;
import com.example.aiecosystem.model.VectorRecord;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class EmbeddingsPipeline {

    private final BedrockModelService modelService;
    private final AiWorkflowProperties properties;

    public EmbeddingsPipeline(BedrockModelService modelService, AiWorkflowProperties properties) {
        this.modelService = modelService;
        this.properties = properties;
    }

    public PipelineState run() throws IOException {
        PipelineState state = new PipelineState();
        loadData(state);
        processContent(state);
        generateEmbeddings(state);
        storeVectors(state);
        generateReport(state);
        return state;
    }

    private void loadData(PipelineState state) throws IOException {
        System.out.println("Loading data from Data Science analysis...");
        if (!Files.exists(ProjectPaths.DATA_SCIENCE_OUTPUT)) {
            throw new IOException("Data Science output not found. Run Exercise 1 first.");
        }
        state.setInputData(JacksonConfig.MAPPER.readValue(ProjectPaths.DATA_SCIENCE_OUTPUT.toFile(), DataScienceExport.class));
        System.out.printf("Loaded %d articles from analysis%n", state.getInputData().contentData().size());
    }

    private void processContent(PipelineState state) {
        System.out.println("Processing content for embeddings...");
        for (ContentAnalysisResult article : state.getInputData().contentData()) {
            System.out.printf("Processing: %s%n", article.title());
            List<String> chunks = createSimpleChunks(article.content(), 500);
            for (int i = 0; i < chunks.size(); i++) {
                String chunk = chunks.get(i);
                ChunkMetadata metadata = new ChunkMetadata(
                        article.title(),
                        article.url(),
                        i,
                        chunks.size(),
                        article.topKeywords(),
                        article.qualityScore(),
                        countWords(chunk),
                        Instant.now()
                );
                state.getProcessedContent().add(new ContentChunk(UUID.randomUUID().toString(), chunk, metadata));
            }
            state.incrementProcessed();
        }
        System.out.printf("Processed %d content chunks%n", state.getProcessedContent().size());
    }

    private void generateEmbeddings(PipelineState state) {
        System.out.println("Generating embeddings with LangChain4j + Bedrock...");
        for (ContentChunk chunk : state.getProcessedContent()) {
            try {
                List<Double> embedding = modelService.embed(chunk.content());
                state.getEmbeddings().add(new VectorRecord(chunk.id(), embedding, chunk.content(), chunk.metadata()));
                state.incrementEmbedded();
                Thread.sleep(100);
            } catch (RuntimeException | InterruptedException ex) {
                if (ex instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                }
                state.getErrors().add("generateEmbeddings: " + ex.getMessage());
                state.incrementFailed();
            }
        }
        System.out.printf("Generated %d embeddings%n", state.getEmbeddings().size());
    }

    private void storeVectors(PipelineState state) throws IOException {
        System.out.println("Storing vectors in local Chroma-compatible JSON fallback...");
        Files.createDirectories(ProjectPaths.CHROMA_FALLBACK_DIR);
        VectorCollection collection = new VectorCollection(
                properties.getVector().getCollectionName(),
                Map.of("description", "Tech content embeddings for semantic search"),
                state.getEmbeddings(),
                Instant.now(),
                state.getEmbeddings().size()
        );
        JacksonConfig.MAPPER.writeValue(
                ProjectPaths.CHROMA_FALLBACK_DIR.resolve(properties.getVector().getCollectionName() + ".json").toFile(),
                collection
        );
        state.setStored(state.getEmbeddings().size());
        System.out.printf("Stored %d vectors%n", state.getStored());
    }

    private void generateReport(PipelineState state) throws IOException {
        long seconds = Duration.between(state.getStartTime(), Instant.now()).toSeconds();
        Map<String, Object> report = Map.of(
                "pipelineRun", Map.of(
                        "timestamp", Instant.now(),
                        "status", state.getErrors().isEmpty() ? "SUCCESS" : "PARTIAL_SUCCESS",
                        "duration", seconds + " seconds"
                ),
                "metrics", Map.of(
                        "processed", state.getProcessed(),
                        "embedded", state.getEmbedded(),
                        "stored", state.getStored(),
                        "failed", state.getFailed()
                ),
                "vectorDatabase", Map.of(
                        "collection", properties.getVector().getCollectionName(),
                        "totalVectors", state.getStored(),
                        "dimensions", state.getEmbeddings().isEmpty() ? properties.getVector().getDimension() : state.getEmbeddings().get(0).vector().size(),
                        "ready", state.getStored() > 0
                ),
                "errors", new ArrayList<>(state.getErrors())
        );

        Files.createDirectories(ProjectPaths.EXERCISE_3);
        JacksonConfig.MAPPER.writeValue(ProjectPaths.PIPELINE_REPORT.toFile(), report);
        JacksonConfig.MAPPER.writeValue(ProjectPaths.VECTOR_DB_CONFIG.toFile(), Map.of(
                "collection", properties.getVector().getCollectionName(),
                "vectorCount", state.getStored(),
                "ready", state.getStored() > 0,
                "createdAt", Instant.now()
        ));
        System.out.printf("Pipeline report written to %s%n", ProjectPaths.PIPELINE_REPORT);
    }

    public List<String> createSimpleChunks(String content, int maxWords) {
        String[] sentences = content.split("[.!?]+");
        List<String> chunks = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        int currentWordCount = 0;

        for (String sentence : sentences) {
            String trimmed = sentence.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            int sentenceWords = countWords(trimmed);
            if (currentWordCount + sentenceWords > maxWords && !current.isEmpty()) {
                chunks.add(current.toString().trim());
                current.setLength(0);
                currentWordCount = 0;
            }
            if (!current.isEmpty()) {
                current.append(". ");
            }
            current.append(trimmed);
            currentWordCount += sentenceWords;
        }

        if (!current.isEmpty()) {
            chunks.add(current.toString().trim());
        }
        return chunks.isEmpty() ? List.of(content) : chunks;
    }

    private static int countWords(String content) {
        if (content == null || content.isBlank()) {
            return 0;
        }
        return content.trim().split("\\s+").length;
    }
}
