package com.example.aiecosystem.appdev;

import com.example.aiecosystem.config.AiWorkflowProperties;
import com.example.aiecosystem.config.JacksonConfig;
import com.example.aiecosystem.config.ProjectPaths;
import com.example.aiecosystem.model.VectorCollection;
import com.example.aiecosystem.model.VectorRecord;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

@Component
public class LocalVectorStore {

    private final AiWorkflowProperties properties;
    private VectorCollection collection;

    public LocalVectorStore(AiWorkflowProperties properties) {
        this.properties = properties;
    }

    public boolean isReady() {
        try {
            return !load().embeddings().isEmpty();
        } catch (IOException ex) {
            return false;
        }
    }

    public int count() {
        try {
            return load().embeddings().size();
        } catch (IOException ex) {
            return 0;
        }
    }

    public List<RagSearchResult> search(List<Double> queryEmbedding, int limit) throws IOException {
        return load().embeddings().stream()
                .map(record -> new RagSearchResult(record, cosineSimilarity(queryEmbedding, record.vector())))
                .sorted(Comparator.comparingDouble(RagSearchResult::similarity).reversed())
                .limit(limit)
                .toList();
    }

    private VectorCollection load() throws IOException {
        if (collection != null) {
            return collection;
        }
        Path path = ProjectPaths.CHROMA_FALLBACK_DIR.resolve(properties.getVector().getCollectionName() + ".json");
        collection = JacksonConfig.MAPPER.readValue(path.toFile(), VectorCollection.class);
        return collection;
    }

    private static double cosineSimilarity(List<Double> a, List<Double> b) {
        if (a == null || b == null || a.size() != b.size()) {
            return -1;
        }
        double dot = 0;
        double normA = 0;
        double normB = 0;
        for (int i = 0; i < a.size(); i++) {
            double x = a.get(i);
            double y = b.get(i);
            dot += x * y;
            normA += x * x;
            normB += y * y;
        }
        return dot / ((Math.sqrt(normA) * Math.sqrt(normB)) + 1e-12);
    }
}
