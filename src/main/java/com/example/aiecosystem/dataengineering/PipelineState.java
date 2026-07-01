package com.example.aiecosystem.dataengineering;

import com.example.aiecosystem.model.ContentChunk;
import com.example.aiecosystem.model.DataScienceExport;
import com.example.aiecosystem.model.VectorRecord;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class PipelineState {

    private DataScienceExport inputData;
    private final List<ContentChunk> processedContent = new ArrayList<>();
    private final List<VectorRecord> embeddings = new ArrayList<>();
    private final List<String> errors = new ArrayList<>();
    private final Instant startTime = Instant.now();
    private int processed;
    private int embedded;
    private int stored;
    private int failed;

    public DataScienceExport getInputData() {
        return inputData;
    }

    public void setInputData(DataScienceExport inputData) {
        this.inputData = inputData;
    }

    public List<ContentChunk> getProcessedContent() {
        return processedContent;
    }

    public List<VectorRecord> getEmbeddings() {
        return embeddings;
    }

    public List<String> getErrors() {
        return errors;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public int getProcessed() {
        return processed;
    }

    public void incrementProcessed() {
        processed++;
    }

    public int getEmbedded() {
        return embedded;
    }

    public void incrementEmbedded() {
        embedded++;
    }

    public int getStored() {
        return stored;
    }

    public void setStored(int stored) {
        this.stored = stored;
    }

    public int getFailed() {
        return failed;
    }

    public void incrementFailed() {
        failed++;
    }
}
