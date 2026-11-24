package com.sqlens.dto;

import java.time.LocalDateTime;

public class QueryHistoryItemDto {
    private String id;
    private String query;
    private double executionTimeMs;
    private double planningTimeMs;
    private long rowsReturned;
    private String status;
    private String errorMessage;
    private String primaryOperation;
    private LocalDateTime timestamp;

    public QueryHistoryItemDto() {}

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final QueryHistoryItemDto target = new QueryHistoryItemDto();

        public Builder id(String val) { target.id = val; return this; }
        public Builder query(String val) { target.query = val; return this; }
        public Builder executionTimeMs(double val) { target.executionTimeMs = val; return this; }
        public Builder planningTimeMs(double val) { target.planningTimeMs = val; return this; }
        public Builder rowsReturned(long val) { target.rowsReturned = val; return this; }
        public Builder status(String val) { target.status = val; return this; }
        public Builder errorMessage(String val) { target.errorMessage = val; return this; }
        public Builder primaryOperation(String val) { target.primaryOperation = val; return this; }
        public Builder timestamp(LocalDateTime val) { target.timestamp = val; return this; }

        public QueryHistoryItemDto build() { return target; }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }
    public double getExecutionTimeMs() { return executionTimeMs; }
    public void setExecutionTimeMs(double executionTimeMs) { this.executionTimeMs = executionTimeMs; }
    public double getPlanningTimeMs() { return planningTimeMs; }
    public void setPlanningTimeMs(double planningTimeMs) { this.planningTimeMs = planningTimeMs; }
    public long getRowsReturned() { return rowsReturned; }
    public void setRowsReturned(long rowsReturned) { this.rowsReturned = rowsReturned; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public String getPrimaryOperation() { return primaryOperation; }
    public void setPrimaryOperation(String primaryOperation) { this.primaryOperation = primaryOperation; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
