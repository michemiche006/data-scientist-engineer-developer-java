# Exercise 1: Data Science - Web Data Exploration

**Role:** Data Scientist  
**Time:** 20-30 minutes  
**Goal:** Analyze web content to identify useful, high-quality content for the embedding pipeline.

## Learning Objectives

- Fetch and parse web pages with Java and jsoup.
- Generate statistics from unstructured text.
- Extract keywords and quality signals for downstream processing.
- Export a structured handoff file for the data engineering stage.

## Your Mission

You are analyzing technology and AI content to understand what should be embedded. The Java implementation fetches sample pages, removes non-content HTML, computes word counts and keyword frequencies, scores content quality, and writes a JSON export for Exercise 2.

## Quick Start

Run from the project root:

```sh
mvn exec:java -Dexec.mainClass=com.example.aiecosystem.datascience.Exercise1DataScience
```

This exercise does not require a Bedrock API key. It only uses Java, Maven, and jsoup.

## Key Java Files

- `src/main/java/com/example/aiecosystem/datascience/Exercise1DataScience.java`
- `src/main/java/com/example/aiecosystem/datascience/WebContentAnalyzer.java`
- `src/main/java/com/example/aiecosystem/datascience/DataAnalysisTools.java`
- `src/main/java/com/example/aiecosystem/model/ContentAnalysisResult.java`
- `src/main/java/com/example/aiecosystem/model/DataScienceExport.java`

## Expected Output

```text
==================================================
   EXERCISE 1: DATA SCIENCE - WEB ANALYSIS
==================================================
Analyzed 3 articles
Average length: 1200 words
Average quality: 8.0/10
Top topics: [bedrock, model, data, ai, embeddings]
Exported 3 high-quality articles to exercise-2-data-engineering/data-science-output.json
```

## Generated Handoff

Exercise 1 creates:

- `exercise-2-data-engineering/data-science-output.json`

That file contains:

- `analysis`: summary statistics and recommendation
- `contentData`: high-quality articles that should become embeddings
- `exportedAt`: handoff timestamp
- `nextStep`: pointer to Exercise 2

## Success Criteria

- Web pages are fetched and parsed without stopping the run on failed URLs.
- Each article has word counts, keyword lists, readability, and quality scores.
- Low-quality content is filtered out.
- `data-science-output.json` exists for Exercise 2.

## Troubleshooting

- If a site blocks scraping or times out, the analyzer logs the failure and continues.
- If no content is exported, check the sample URLs or lower the quality threshold in `WebContentAnalyzer`.
- Run commands from the project root so generated files land in the expected directories.

**Next:** Run Exercise 2 to turn the exported content into searchable embeddings.
