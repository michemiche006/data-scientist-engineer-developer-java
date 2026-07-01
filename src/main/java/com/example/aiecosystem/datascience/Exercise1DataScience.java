package com.example.aiecosystem.datascience;

import com.example.aiecosystem.model.DataScienceExport;

public class Exercise1DataScience {

    public static void main(String[] args) throws Exception {
        System.out.println("==================================================");
        System.out.println("   EXERCISE 1: DATA SCIENCE - WEB ANALYSIS");
        System.out.println("==================================================");
        System.out.println("Role: Data Scientist");
        System.out.println("Task: Analyze web content for embedding pipeline");

        DataScienceExport export = new WebContentAnalyzer().fetchAnalyzeAndExport();

        System.out.printf("Analyzed %d articles%n", export.analysis().totalArticles());
        System.out.printf("Average length: %d words%n", export.analysis().avgWordCount());
        System.out.printf("Average quality: %.1f/10%n", export.analysis().avgQualityScore());
        System.out.printf("Top topics: %s%n", export.analysis().topTopics());
        System.out.printf("Recommendation: %s%n", export.analysis().recommendation());
        System.out.printf("Exported %d high-quality articles to exercise-2-data-engineering/data-science-output.json%n",
                export.contentData().size());
    }
}
