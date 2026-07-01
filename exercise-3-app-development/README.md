# Exercise 3: Application Development - RAG Chat Agent

**Role:** Application Developer  
**Time:** 30-45 minutes  
**Goal:** Build and run a Spring Boot chat API that answers questions using retrieved vector context and Bedrock chat responses.

## Learning Objectives

- Load a vector-store handoff from the data engineering stage.
- Embed user queries with LangChain4j and Bedrock.
- Rank stored content chunks with cosine similarity.
- Build RAG prompts with source context.
- Expose chat, health, and conversation-history endpoints with Spring Boot.

## Bedrock API Key

This exercise uses the same Bedrock API key setup as Exercise 2:
export the variables in your shell or put them in a local `.env` file copied from `.env.example`. The Spring Boot app loads `.env` automatically.

```sh
export AWS_BEARER_TOKEN_BEDROCK="your_bedrock_api_key"
export BEDROCK_AWS_REGION="us-east-1"
export BEDROCK_CHAT_MODEL_ID="us.amazon.nova-lite-v1:0"
export BEDROCK_EMBEDDINGS_MODEL_ID="amazon.titan-embed-text-v2:0"
```

If Bedrock is unavailable, the app keeps running with local embedding and chat fallbacks so you can still inspect retrieval, metadata, and API behavior.

## Prerequisites

Run Exercise 1 and Exercise 2 first:

```sh
mvn exec:java -Dexec.mainClass=com.example.aiecosystem.datascience.Exercise1DataScience
mvn exec:java -Dexec.mainClass=com.example.aiecosystem.dataengineering.Exercise2DataEngineering
```

Confirm these files exist:

- `exercise-2-data-engineering/chroma_db/tech-content-vectors.json`
- `exercise-3-app-development/vector-db-config.json`

## Quick Start

Run from the project root:

```sh
mvn spring-boot:run
```

Open the browser UI:

```text
http://localhost:3000
```

Or test the chat endpoint:

```sh
curl -X POST http://localhost:3000/chat \
  -H "Content-Type: application/json" \
  -d '{"message":"What is Amazon Bedrock?"}'
```

## Key Java Files

- `src/main/java/com/example/aiecosystem/AiEcosystemApplication.java`
- `src/main/java/com/example/aiecosystem/appdev/ChatController.java`
- `src/main/java/com/example/aiecosystem/appdev/RagChatAgent.java`
- `src/main/java/com/example/aiecosystem/appdev/LocalVectorStore.java`
- `src/main/java/com/example/aiecosystem/appdev/ChatDtos.java`
- `src/main/java/com/example/aiecosystem/config/BedrockModelService.java`

## API Endpoints

- `GET /health`: returns app status, vector readiness, and vector count.
- `POST /chat`: accepts `{ "message": "...", "conversationId": "optional" }`.
- `GET /conversations/{conversationId}`: returns stored turns for a conversation.
- `GET /`: serves a simple browser chat UI.

## Expected Output

```text
Started AiEcosystemApplication
Tomcat started on port 3000
```

Health response:

```json
{
  "status": "ok",
  "agentReady": true,
  "vectorCount": 8
}
```

## Success Criteria

- `/health` reports `agentReady: true`.
- `/chat` returns an answer, a conversation ID, metadata, and source attributions.
- Follow-up requests can reuse the returned `conversationId`.
- `/conversations/{conversationId}` returns recent turns.
- The browser UI can send messages to the Spring Boot backend.

## Troubleshooting

- If `agentReady` is false, rerun Exercise 2 and check the generated vector files.
- If chat responses mention a local fallback, verify `AWS_BEARER_TOKEN_BEDROCK` and `BEDROCK_CHAT_MODEL_ID`.
- If port `3000` is busy, set `SERVER_PORT` before running Spring Boot.

**Complete:** You now have the full Java workflow from web analysis to embeddings to a RAG chat application.
