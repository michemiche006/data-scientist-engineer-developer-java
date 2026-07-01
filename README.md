# Data Scientist, Engineer, Developer - Java

Java/Maven version of the AI/ML collaboration workshop. It keeps the same three-role flow as the JavaScript project, but uses LangChain4j and a Bedrock API key instead of AWS access-key secrets.

## Stack

- Java 17 and Maven
- Spring Boot for the RAG chat API
- LangChain4j for Bedrock chat and Titan embeddings
- `AWS_BEARER_TOKEN_BEDROCK` for Bedrock API key authentication
- Local JSON vector storage for the tutorial handoff between exercises

## Bedrock API Key

This project does not require `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`, or AWS Secrets Manager.
You can either export these values in your shell or create a local `.env` file from `.env.example`; the Java app loads `.env` automatically.

```sh
export AWS_BEARER_TOKEN_BEDROCK="your_bedrock_api_key"
export BEDROCK_AWS_REGION="us-east-1"
export BEDROCK_CHAT_MODEL_ID="us.amazon.nova-lite-v1:0"
export BEDROCK_EMBEDDINGS_MODEL_ID="amazon.titan-embed-text-v2:0"
```

If the API key is missing or a Bedrock call fails, the tutorial uses deterministic local embeddings and a local chat fallback so the exercise flow can still be demonstrated.

## Workshop Flow

Exercise 1, Data Science: fetch technology content, parse text with jsoup, calculate keywords and quality metrics, and export high-quality content for embedding. See `exercise-1-data-science/README.md` for the full guide.

```sh
mvn exec:java -Dexec.mainClass=com.example.aiecosystem.datascience.Exercise1DataScience
```

Exercise 2, Data Engineering: load the Exercise 1 export, chunk content, generate LangChain4j Bedrock embeddings, store vectors with metadata, and write a pipeline report. See `exercise-2-data-engineering/README.md` for the full guide.

```sh
mvn exec:java -Dexec.mainClass=com.example.aiecosystem.dataengineering.Exercise2DataEngineering
```

Exercise 3, App Development: start the Spring Boot chat API, retrieve similar vector chunks, call the Bedrock chat model, track conversation memory, and return source attribution. See `exercise-3-app-development/README.md` for the full guide.

```sh
mvn spring-boot:run
```

Then open `http://localhost:3000` or call:

```sh
curl -X POST http://localhost:3000/chat \
  -H "Content-Type: application/json" \
  -d '{"message":"What is Amazon Bedrock?"}'
```

## Build

```sh
mvn -DskipTests package
```

## Project Layout

```text
exercise-1-data-science/    # Exercise 1 instructions
exercise-2-data-engineering/ # Exercise 2 instructions and generated pipeline output
exercise-3-app-development/  # Exercise 3 instructions and generated app handoff
src/main/java/com/example/aiecosystem/
├── config/            # Bedrock, LangChain4j, JSON, and path configuration
├── datascience/       # Exercise 1 entry point and web analysis code
├── dataengineering/   # Exercise 2 embedding pipeline
├── appdev/            # Exercise 3 RAG agent and REST controller
└── model/             # Shared handoff records
```

## Generated Files

The exercise output directories are created only when the workflow runs:

- `exercise-2-data-engineering/data-science-output.json`
- `exercise-2-data-engineering/pipeline-report.json`
- `exercise-2-data-engineering/chroma_db/tech-content-vectors.json`
- `exercise-3-app-development/vector-db-config.json`
