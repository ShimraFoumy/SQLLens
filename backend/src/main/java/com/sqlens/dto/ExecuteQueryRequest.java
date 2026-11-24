package com.sqlens.dto;

public class ExecuteQueryRequest {
    private String query;
    private boolean analyze = true;

    public ExecuteQueryRequest() {}

    public ExecuteQueryRequest(String query, boolean analyze) {
        this.query = query;
        this.analyze = analyze;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public boolean isAnalyze() {
        return analyze;
    }

    public void setAnalyze(boolean analyze) {
        this.analyze = analyze;
    }
}
