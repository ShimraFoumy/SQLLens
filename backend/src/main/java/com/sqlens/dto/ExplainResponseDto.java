package com.sqlens.dto;

import java.util.ArrayList;
import java.util.List;

public class ExplainResponseDto {
    private PerformanceSummaryDto summary;
    private PlanNodeDto plan;
    private List<OptimizationTipDto> optimizationTips = new ArrayList<>();
    private String rawJson;
    private String rawText;

    public ExplainResponseDto() {}

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final ExplainResponseDto target = new ExplainResponseDto();

        public Builder summary(PerformanceSummaryDto val) { target.summary = val; return this; }
        public Builder plan(PlanNodeDto val) { target.plan = val; return this; }
        public Builder optimizationTips(List<OptimizationTipDto> val) { target.optimizationTips = val; return this; }
        public Builder rawJson(String val) { target.rawJson = val; return this; }
        public Builder rawText(String val) { target.rawText = val; return this; }

        public ExplainResponseDto build() { return target; }
    }

    public PerformanceSummaryDto getSummary() { return summary; }
    public void setSummary(PerformanceSummaryDto summary) { this.summary = summary; }
    public PlanNodeDto getPlan() { return plan; }
    public void setPlan(PlanNodeDto plan) { this.plan = plan; }
    public List<OptimizationTipDto> getOptimizationTips() { return optimizationTips; }
    public void setOptimizationTips(List<OptimizationTipDto> optimizationTips) { this.optimizationTips = optimizationTips; }
    public String getRawJson() { return rawJson; }
    public void setRawJson(String rawJson) { this.rawJson = rawJson; }
    public String getRawText() { return rawText; }
    public void setRawText(String rawText) { this.rawText = rawText; }
}
