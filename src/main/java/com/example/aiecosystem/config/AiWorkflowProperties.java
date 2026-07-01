package com.example.aiecosystem.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public class AiWorkflowProperties {

    private final Bedrock bedrock = new Bedrock();
    private final Vector vector = new Vector();

    public Bedrock getBedrock() {
        return bedrock;
    }

    public Vector getVector() {
        return vector;
    }

    public static class Bedrock {
        private String region = "us-east-1";
        private String chatModelId = "us.amazon.nova-lite-v1:0";
        private String embeddingsModelId = "amazon.titan-embed-text-v2:0";

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public String getChatModelId() {
            return chatModelId;
        }

        public void setChatModelId(String chatModelId) {
            this.chatModelId = chatModelId;
        }

        public String getEmbeddingsModelId() {
            return embeddingsModelId;
        }

        public void setEmbeddingsModelId(String embeddingsModelId) {
            this.embeddingsModelId = embeddingsModelId;
        }
    }

    public static class Vector {
        private String collectionName = "tech-content-vectors";
        private int dimension = 1024;

        public String getCollectionName() {
            return collectionName;
        }

        public void setCollectionName(String collectionName) {
            this.collectionName = collectionName;
        }

        public int getDimension() {
            return dimension;
        }

        public void setDimension(int dimension) {
            this.dimension = dimension;
        }
    }
}
