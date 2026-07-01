package com.example.aiecosystem.datascience;

import com.example.aiecosystem.config.JacksonConfig;
import com.example.aiecosystem.config.ProjectPaths;
import com.example.aiecosystem.model.AnalysisSummary;
import com.example.aiecosystem.model.ContentAnalysisResult;
import com.example.aiecosystem.model.ContentArticle;
import com.example.aiecosystem.model.DataScienceExport;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class WebContentAnalyzer {

    private static final List<String> SAMPLE_URLS = List.of(
            "https://aws.amazon.com/blogs/machine-learning/",
            "https://docs.aws.amazon.com/bedrock/latest/userguide/what-is-bedrock.html",
            "https://docs.langchain4j.dev/"
    );

    private static final Set<String> STOP_WORDS = Set.of(
            "about", "after", "again", "also", "because", "before", "being", "between", "could",
            "every", "from", "have", "into", "more", "most", "only", "other", "over", "such",
            "than", "that", "their", "there", "these", "this", "through", "using", "when", "where",
            "which", "while", "with", "your"
    );

    public DataScienceExport fetchAnalyzeAndExport() throws IOException {
        List<ContentAnalysisResult> results = new ArrayList<>();
        for (String url : SAMPLE_URLS) {
            fetchWebContent(url).ifPresent(article -> results.add(analyzeTextContent(article)));
            sleep(1_000);
        }

        AnalysisSummary summary = generateInsights(results);
        List<ContentAnalysisResult> highQualityContent = results.stream()
                .filter(result -> result.qualityScore() >= 6)
                .toList();

        DataScienceExport export = new DataScienceExport(
                summary,
                highQualityContent,
                Instant.now(),
                "Use this data in exercise-2-data-engineering"
        );

        Files.createDirectories(ProjectPaths.EXERCISE_2);
        JacksonConfig.MAPPER.writeValue(ProjectPaths.DATA_SCIENCE_OUTPUT.toFile(), export);
        return export;
    }

    public java.util.Optional<ContentArticle> fetchWebContent(String url) {
        try {
            System.out.printf("Fetching content from: %s%n", url);
            Document document = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 LangChain4j Java Tutorial")
                    .timeout(15_000)
                    .get();
            document.select("script,style,noscript,svg").remove();

            String title = document.title();
            String content = document.body().text().replaceAll("\\s+", " ").trim();
            return java.util.Optional.of(new ContentArticle(url, title, content, countWords(content), Instant.now()));
        } catch (IOException ex) {
            System.out.printf("Failed to fetch %s: %s%n", url, ex.getMessage());
            return java.util.Optional.empty();
        }
    }

    public ContentAnalysisResult analyzeTextContent(ContentArticle article) {
        System.out.printf("Analyzing: %s%n", article.title());
        List<String> words = tokenize(article.content());
        Map<String, Long> frequencies = words.stream()
                .collect(Collectors.groupingBy(word -> word, HashMap::new, Collectors.counting()));
        List<String> topKeywords = frequencies.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(8)
                .map(Map.Entry::getKey)
                .toList();

        int sentenceCount = Math.max(1, article.content().split("[.!?]+").length);
        double avgWordsPerSentence = (double) article.wordCount() / sentenceCount;

        return new ContentAnalysisResult(
                article.url(),
                article.title(),
                article.content(),
                article.wordCount(),
                (int) words.stream().distinct().count(),
                topKeywords,
                Math.min(10, avgWordsPerSentence / 2),
                calculateQualityScore(article),
                Instant.now()
        );
    }

    public AnalysisSummary generateInsights(List<ContentAnalysisResult> results) {
        List<Integer> wordCounts = results.stream().map(ContentAnalysisResult::wordCount).toList();
        List<Double> qualityScores = results.stream().map(ContentAnalysisResult::qualityScore).toList();
        double avgQuality = DataAnalysisTools.mean(qualityScores);

        return new AnalysisSummary(
                results.size(),
                (int) Math.round(DataAnalysisTools.mean(wordCounts)),
                Math.round(avgQuality * 10.0) / 10.0,
                extractTopTopics(results),
                generateRecommendation(avgQuality),
                Instant.now()
        );
    }

    private List<String> extractTopTopics(List<ContentAnalysisResult> results) {
        Map<String, Long> topicCounts = results.stream()
                .flatMap(result -> result.topKeywords().stream())
                .collect(Collectors.groupingBy(keyword -> keyword, LinkedHashMap::new, Collectors.counting()));

        return topicCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder()))
                .limit(5)
                .map(Map.Entry::getKey)
                .toList();
    }

    private String generateRecommendation(double avgQuality) {
        if (avgQuality >= 7) {
            return "HIGH: Process all articles for embeddings - excellent content quality";
        }
        if (avgQuality >= 5) {
            return "MEDIUM: Process articles with quality score > 6 for embeddings";
        }
        return "LOW: Review content sources - quality below threshold";
    }

    private double calculateQualityScore(ContentArticle article) {
        double score = 5;
        if (article.wordCount() > 500) {
            score += 2;
        }
        if (article.wordCount() > 1_000) {
            score += 1;
        }
        if (article.title() != null && article.title().length() > 20) {
            score += 1;
        }

        String lower = article.content().toLowerCase(Locale.ROOT);
        List<String> technicalWords = List.of("algorithm", "model", "data", "analysis", "machine", "learning", "embedding", "vector");
        long technicalHits = technicalWords.stream().filter(lower::contains).count();
        score += Math.min(2, technicalHits * 0.5);
        return Math.min(10, Math.max(1, score));
    }

    private List<String> tokenize(String text) {
        return List.of(text.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9\\s]", " ").split("\\s+"))
                .stream()
                .filter(word -> word.length() > 3)
                .filter(word -> !STOP_WORDS.contains(word))
                .toList();
    }

    private static int countWords(String content) {
        if (content == null || content.isBlank()) {
            return 0;
        }
        return content.trim().split("\\s+").length;
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}
