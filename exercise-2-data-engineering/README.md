# Exercise 2: Data Engineering - Embeddings Pipeline

**Role:** Data Engineer  
**Time:** 30-45 minutes  
**Goal:** Transform analyzed content into searchable vectors using LangChain4j and Amazon Bedrock.

## Learning Objectives

- Load a structured handoff from the data science stage.
- Chunk documents into embedding-friendly segments.
- Generate embeddings with LangChain4j's Bedrock Titan integration.
- Store vectors with metadata for retrieval by the chat agent.
- Produce pipeline metrics and an app-development handoff.

## Bedrock API Key

This Java version uses a Bedrock API key instead of AWS access-key secrets or AWS Secrets Manager.
You can export the variables in your shell or put them in a local `.env` file copied from `.env.example`; the app loads `.env` automatically.

```sh
export AWS_BEARER_TOKEN_BEDROCK="your_bedrock_api_key"
export BEDROCK_AWS_REGION="us-east-1"
export BEDROCK_EMBEDDINGS_MODEL_ID="amazon.titan-embed-text-v2:0"
```

If `AWS_BEARER_TOKEN_BEDROCK` is missing or Bedrock is unavailable, the pipeline falls back to deterministic local embeddings so the tutorial can still run end-to-end.

## Prerequisite

Run Exercise 1 first:

```sh
mvn exec:java -Dexec.mainClass=com.example.aiecosystem.datascience.Exercise1DataScience
```

Confirm this file exists:

- `exercise-2-data-engineering/data-science-output.json`

## Quick Start

Run from the project root:

```sh
mvn exec:java -Dexec.mainClass=com.example.aiecosystem.dataengineering.Exercise2DataEngineering
```

## Key Java Files

- `src/main/java/com/example/aiecosystem/dataengineering/Exercise2DataEngineering.java`
- `src/main/java/com/example/aiecosystem/dataengineering/EmbeddingsPipeline.java`
- `src/main/java/com/example/aiecosystem/dataengineering/PipelineState.java`
- `src/main/java/com/example/aiecosystem/config/BedrockModelService.java`
- `src/main/java/com/example/aiecosystem/config/LangChain4jConfig.java`
- `src/main/java/com/example/aiecosystem/model/VectorRecord.java`
- `src/main/java/com/example/aiecosystem/model/VectorCollection.java`

## Pipeline Stages

1. `loadData`: reads the Exercise 1 JSON export.
2. `processContent`: splits articles into sentence-aware chunks.
3. `generateEmbeddings`: calls LangChain4j Bedrock Titan embeddings or the local fallback.
4. `storeVectors`: writes a local Chroma-compatible JSON vector collection.
5. `generateReport`: writes metrics and the Exercise 3 handoff.

## Expected Output

```text
==================================================
  EXERCISE 2: DATA ENGINEERING - EMBEDDINGS
==================================================
Loaded 3 articles from analysis
Processed 8 content chunks
Generated 8 embeddings
Stored 8 vectors
Pipeline complete: processed=3 embedded=8 stored=8 errors=0
```

## Generated Files

- `exercise-2-data-engineering/pipeline-report.json`
- `exercise-2-data-engineering/chroma_db/tech-content-vectors.json`
- `exercise-3-app-development/vector-db-config.json`

## Success Criteria

- Exercise 1 data is loaded successfully.
- Content is chunked with metadata for title, URL, keywords, quality score, and chunk index.
- Embeddings are generated through Bedrock or the documented local fallback.
- Vector records are stored in `tech-content-vectors.json`.
- Exercise 3 can verify the vector database handoff.

## Troubleshooting

- If the input file is missing, run Exercise 1 from the project root.
- If Bedrock returns an auth error, verify `AWS_BEARER_TOKEN_BEDROCK` and model access in the Bedrock console.
- If you change embedding models, update `BEDROCK_EMBEDDINGS_MODEL_ID` and rerun Exercise 2 before starting Exercise 3.

**Next:** Run Exercise 3 to query the vector collection through a RAG chat API.
