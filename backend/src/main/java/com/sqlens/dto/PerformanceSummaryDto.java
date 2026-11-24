package com.sqlens.dto;

public class PerformanceSummaryDto {
    private double planningTimeMs;
    private double executionTimeMs;
    private double totalCost;
    private long rowsReturned;
    private int totalOperations;

    private String primaryOperation;
    private String primaryScanType;
    private String bottleneckNode;
    private double bottleneckTimeMs;

    private long totalRowsScanned;
    private long totalRowsRemovedByFilter;
    private double bufferHitRatio;
    private boolean usesIndex;

    public PerformanceSummaryDto() {}

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final PerformanceSummaryDto target = new PerformanceSummaryDto();

        public Builder planningTimeMs(double val) { target.planningTimeMs = val; return this; }
        public Builder executionTimeMs(double val) { target.executionTimeMs = val; return this; }
        public Builder totalCost(double val) { target.totalCost = val; return this; }
        public Builder rowsReturned(long val) { target.rowsReturned = val; return this; }
        public Builder totalOperations(int val) { target.totalOperations = val; return this; }
        public Builder primaryOperation(String val) { target.primaryOperation = val; return this; }
        public Builder primaryScanType(String val) { target.primaryScanType = val; return this; }
        public Builder bottleneckNode(String val) { target.bottleneckNode = val; return this; }
        public Builder bottleneckTimeMs(double val) { target.bottleneckTimeMs = val; return this; }
        public Builder totalRowsScanned(long val) { target.totalRowsScanned = val; return this; }
        public Builder totalRowsRemovedByFilter(long val) { target.totalRowsRemovedByFilter = val; return this; }
        public Builder bufferHitRatio(double val) { target.bufferHitRatio = val; return this; }
        public Builder usesIndex(boolean val) { target.usesIndex = val; return this; }

        public PerformanceSummaryDto build() { return target; }
    }

    // Getters and Setters
    public double getPlanningTimeMs() { return planningTimeMs; }
    public void setPlanningTimeMs(double planningTimeMs) { this.planningTimeMs = planningTimeMs; }
    public double getExecutionTimeMs() { return executionTimeMs; }
    public void setExecutionTimeMs(double executionTimeMs) { this.executionTimeMs = executionTimeMs; }
    public double getTotalCost() { return totalCost; }
    public void setTotalCost(double totalCost) { this.totalCost = totalCost; }
    public long getRowsReturned() { return rowsReturned; }
    public void setRowsReturned(long rowsReturned) { this.rowsReturned = rowsReturned; }
    public int getTotalOperations() { return totalOperations; }
    public void setTotalOperations(int totalOperations) { this.totalOperations = totalOperations; }
    public String getPrimaryOperation() { return primaryOperation; }
    public void setPrimaryOperation(String primaryOperation) { this.primaryOperation = primaryOperation; }
    public String getPrimaryScanType() { return primaryScanType; }
    public void setPrimaryScanType(String primaryScanType) { this.primaryScanType = primaryScanType; }
    public String getBottleneckNode() { return bottleneckNode; }
    public void setBottleneckNode(String bottleneckNode) { this.bottleneckNode = bottleneckNode; }
    public double getBottleneckTimeMs() { return bottleneckTimeMs; }
    public void setBottleneckTimeMs(double bottleneckTimeMs) { this.bottleneckTimeMs = bottleneckTimeMs; }
    public long getTotalRowsScanned() { return totalRowsScanned; }
    public void setTotalRowsScanned(long totalRowsScanned) { this.totalRowsScanned = totalRowsScanned; }
    public long getTotalRowsRemovedByFilter() { return totalRowsRemovedByFilter; }
    public void setTotalRowsRemovedByFilter(long totalRowsRemovedByFilter) { this.totalRowsRemovedByFilter = totalRowsRemovedByFilter; }
    public double getBufferHitRatio() { return bufferHitRatio; }
    public void setBufferHitRatio(double bufferHitRatio) { this.bufferHitRatio = bufferHitRatio; }
    public boolean isUsesIndex() { return usesIndex; }
    public void setUsesIndex(boolean usesIndex) { this.usesIndex = usesIndex; }
}
