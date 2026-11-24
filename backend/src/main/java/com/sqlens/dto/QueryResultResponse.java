package com.sqlens.dto;

import java.util.List;
import java.util.Map;

public class QueryResultResponse {
    private double executionTimeMs;
    private long rowsReturned;
    private List<ColumnMetadata> columns;
    private List<Map<String, Object>> rows;
    private boolean truncated;
    private String message;
    private String status;

    public QueryResultResponse() {}

    public QueryResultResponse(double executionTimeMs, long rowsReturned, List<ColumnMetadata> columns,
                               List<Map<String, Object>> rows, boolean truncated, String message, String status) {
        this.executionTimeMs = executionTimeMs;
        this.rowsReturned = rowsReturned;
        this.columns = columns;
        this.rows = rows;
        this.truncated = truncated;
        this.message = message;
        this.status = status;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private double executionTimeMs;
        private long rowsReturned;
        private List<ColumnMetadata> columns;
        private List<Map<String, Object>> rows;
        private boolean truncated;
        private String message;
        private String status;

        public Builder executionTimeMs(double val) { this.executionTimeMs = val; return this; }
        public Builder rowsReturned(long val) { this.rowsReturned = val; return this; }
        public Builder columns(List<ColumnMetadata> val) { this.columns = val; return this; }
        public Builder rows(List<Map<String, Object>> val) { this.rows = val; return this; }
        public Builder truncated(boolean val) { this.truncated = val; return this; }
        public Builder message(String val) { this.message = val; return this; }
        public Builder status(String val) { this.status = val; return this; }

        public QueryResultResponse build() {
            return new QueryResultResponse(executionTimeMs, rowsReturned, columns, rows, truncated, message, status);
        }
    }

    public double getExecutionTimeMs() { return executionTimeMs; }
    public void setExecutionTimeMs(double executionTimeMs) { this.executionTimeMs = executionTimeMs; }
    public long getRowsReturned() { return rowsReturned; }
    public void setRowsReturned(long rowsReturned) { this.rowsReturned = rowsReturned; }
    public List<ColumnMetadata> getColumns() { return columns; }
    public void setColumns(List<ColumnMetadata> columns) { this.columns = columns; }
    public List<Map<String, Object>> getRows() { return rows; }
    public void setRows(List<Map<String, Object>> rows) { this.rows = rows; }
    public boolean isTruncated() { return truncated; }
    public void setTruncated(boolean truncated) { this.truncated = truncated; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
