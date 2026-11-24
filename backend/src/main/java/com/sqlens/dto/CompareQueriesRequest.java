package com.sqlens.dto;

public class CompareQueriesRequest {
    private String queryA;
    private String queryB;

    public CompareQueriesRequest() {}

    public CompareQueriesRequest(String queryA, String queryB) {
        this.queryA = queryA;
        this.queryB = queryB;
    }

    public String getQueryA() { return queryA; }
    public void setQueryA(String queryA) { this.queryA = queryA; }
    public String getQueryB() { return queryB; }
    public void setQueryB(String queryB) { this.queryB = queryB; }
}
