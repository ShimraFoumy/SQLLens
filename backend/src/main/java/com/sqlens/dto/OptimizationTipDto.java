package com.sqlens.dto;

public class OptimizationTipDto {
    private String level; // "INFO", "WARNING", "CRITICAL", "SUCCESS"
    private String title;
    private String message;
    private String recommendation;
    private String affectedNode;

    public OptimizationTipDto() {}

    public OptimizationTipDto(String level, String title, String message, String recommendation, String affectedNode) {
        this.level = level;
        this.title = title;
        this.message = message;
        this.recommendation = recommendation;
        this.affectedNode = affectedNode;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String level;
        private String title;
        private String message;
        private String recommendation;
        private String affectedNode;

        public Builder level(String val) { this.level = val; return this; }
        public Builder title(String val) { this.title = val; return this; }
        public Builder message(String val) { this.message = val; return this; }
        public Builder recommendation(String val) { this.recommendation = val; return this; }
        public Builder affectedNode(String val) { this.affectedNode = val; return this; }

        public OptimizationTipDto build() {
            return new OptimizationTipDto(level, title, message, recommendation, affectedNode);
        }
    }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
    public String getAffectedNode() { return affectedNode; }
    public void setAffectedNode(String affectedNode) { this.affectedNode = affectedNode; }
}
