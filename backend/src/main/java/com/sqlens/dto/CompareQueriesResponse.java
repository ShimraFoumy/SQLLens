package com.sqlens.dto;

public class CompareQueriesResponse {
    private ExplainResponseDto planA;
    private ExplainResponseDto planB;
    private double executionTimeDiffMs;
    private double costDiff;
    private String fasterQuery;
    private double speedupFactor;
    private String comparisonSummary;

    public CompareQueriesResponse() {}

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final CompareQueriesResponse target = new CompareQueriesResponse();

        public Builder planA(ExplainResponseDto val) { target.planA = val; return this; }
        public Builder planB(ExplainResponseDto val) { target.planB = val; return this; }
        public Builder executionTimeDiffMs(double val) { target.executionTimeDiffMs = val; return this; }
        public Builder costDiff(double val) { target.costDiff = val; return this; }
        public Builder fasterQuery(String val) { target.fasterQuery = val; return this; }
        public Builder speedupFactor(double val) { target.speedupFactor = val; return this; }
        public Builder comparisonSummary(String val) { target.comparisonSummary = val; return this; }

        public CompareQueriesResponse build() { return target; }
    }

    public ExplainResponseDto getPlanA() { return planA; }
    public void setPlanA(ExplainResponseDto planA) { this.planA = planA; }
    public ExplainResponseDto getPlanB() { return planB; }
    public void setPlanB(ExplainResponseDto planB) { this.planB = planB; }
    public double getExecutionTimeDiffMs() { return executionTimeDiffMs; }
    public void setExecutionTimeDiffMs(double executionTimeDiffMs) { this.executionTimeDiffMs = executionTimeDiffMs; }
    public double getCostDiff() { return costDiff; }
    public void setCostDiff(double costDiff) { this.costDiff = costDiff; }
    public String getFasterQuery() { return fasterQuery; }
    public void setFasterQuery(String fasterQuery) { this.fasterQuery = fasterQuery; }
    public double getSpeedupFactor() { return speedupFactor; }
    public void setSpeedupFactor(double speedupFactor) { this.speedupFactor = speedupFactor; }
    public String getComparisonSummary() { return comparisonSummary; }
    public void setComparisonSummary(String comparisonSummary) { this.comparisonSummary = comparisonSummary; }
}
