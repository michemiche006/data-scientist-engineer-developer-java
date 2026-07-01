package com.example.aiecosystem.dataengineering;

import com.example.aiecosystem.AiEcosystemApplication;
import com.example.aiecosystem.config.BedrockApiKeyNotice;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public class Exercise2DataEngineering {

    public static void main(String[] args) throws Exception {
        System.out.println("==================================================");
        System.out.println("  EXERCISE 2: DATA ENGINEERING - EMBEDDINGS");
        System.out.println("==================================================");
        System.out.println("Role: Data Engineer");
        System.out.println("Task: Build embeddings pipeline with LangChain4j and Bedrock");
        BedrockApiKeyNotice.printIfMissing();

        try (ConfigurableApplicationContext context = new SpringApplicationBuilder(AiEcosystemApplication.class)
                .web(WebApplicationType.NONE)
                .run(args)) {
            PipelineState state = context.getBean(EmbeddingsPipeline.class).run();
            System.out.printf("Pipeline complete: processed=%d embedded=%d stored=%d errors=%d%n",
                    state.getProcessed(), state.getEmbedded(), state.getStored(), state.getErrors().size());
        }
    }
}
