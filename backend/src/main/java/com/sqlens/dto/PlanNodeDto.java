package com.sqlens.dto;

import java.util.ArrayList;
import java.util.List;

public class PlanNodeDto {
    private String id;
    private String nodeType;
    private String relationName;
    private String schema;
    private String alias;

    private Double startupCost;
    private Double totalCost;
    private Long planRows;
    private Integer planWidth;

    private Double actualStartupTime;
    private Double actualTotalTime;
    private Long actualRows;
    private Long actualLoops;

    private Double exclusiveTimeMs;
    private Double costPercentage;
    private Double timePercentage;

    private String filter;
    private Long rowsRemovedByFilter;
    private String indexName;
    private String indexCond;
    private String scanDirection;

    private String joinType;
    private String hashCond;
    private String sortKey;
    private String sortMethod;
    private String sortSpaceUsed;
    private String sortSpaceType;

    private Long sharedHitBlocks;
    private Long sharedReadBlocks;
    private Long sharedDirtiedBlocks;
    private Long sharedWrittenBlocks;

    private List<PlanNodeDto> plans = new ArrayList<>();

    public PlanNodeDto() {}

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final PlanNodeDto target = new PlanNodeDto();

        public Builder id(String val) { target.id = val; return this; }
        public Builder nodeType(String val) { target.nodeType = val; return this; }
        public Builder relationName(String val) { target.relationName = val; return this; }
        public Builder schema(String val) { target.schema = val; return this; }
        public Builder alias(String val) { target.alias = val; return this; }
        public Builder startupCost(Double val) { target.startupCost = val; return this; }
        public Builder totalCost(Double val) { target.totalCost = val; return this; }
        public Builder planRows(Long val) { target.planRows = val; return this; }
        public Builder planWidth(Integer val) { target.planWidth = val; return this; }
        public Builder actualStartupTime(Double val) { target.actualStartupTime = val; return this; }
        public Builder actualTotalTime(Double val) { target.actualTotalTime = val; return this; }
        public Builder actualRows(Long val) { target.actualRows = val; return this; }
        public Builder actualLoops(Long val) { target.actualLoops = val; return this; }
        public Builder exclusiveTimeMs(Double val) { target.exclusiveTimeMs = val; return this; }
        public Builder costPercentage(Double val) { target.costPercentage = val; return this; }
        public Builder timePercentage(Double val) { target.timePercentage = val; return this; }
        public Builder filter(String val) { target.filter = val; return this; }
        public Builder rowsRemovedByFilter(Long val) { target.rowsRemovedByFilter = val; return this; }
        public Builder indexName(String val) { target.indexName = val; return this; }
        public Builder indexCond(String val) { target.indexCond = val; return this; }
        public Builder scanDirection(String val) { target.scanDirection = val; return this; }
        public Builder joinType(String val) { target.joinType = val; return this; }
        public Builder hashCond(String val) { target.hashCond = val; return this; }
        public Builder sortKey(String val) { target.sortKey = val; return this; }
        public Builder sortMethod(String val) { target.sortMethod = val; return this; }
        public Builder sortSpaceUsed(String val) { target.sortSpaceUsed = val; return this; }
        public Builder sortSpaceType(String val) { target.sortSpaceType = val; return this; }
        public Builder sharedHitBlocks(Long val) { target.sharedHitBlocks = val; return this; }
        public Builder sharedReadBlocks(Long val) { target.sharedReadBlocks = val; return this; }
        public Builder sharedDirtiedBlocks(Long val) { target.sharedDirtiedBlocks = val; return this; }
        public Builder sharedWrittenBlocks(Long val) { target.sharedWrittenBlocks = val; return this; }
        public Builder plans(List<PlanNodeDto> val) { target.plans = val; return this; }

        public PlanNodeDto build() {
            return target;
        }
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNodeType() { return nodeType; }
    public void setNodeType(String nodeType) { this.nodeType = nodeType; }
    public String getRelationName() { return relationName; }
    public void setRelationName(String relationName) { this.relationName = relationName; }
    public String getSchema() { return schema; }
    public void setSchema(String schema) { this.schema = schema; }
    public String getAlias() { return alias; }
    public void setAlias(String alias) { this.alias = alias; }
    public Double getStartupCost() { return startupCost; }
    public void setStartupCost(Double startupCost) { this.startupCost = startupCost; }
    public Double getTotalCost() { return totalCost; }
    public void setTotalCost(Double totalCost) { this.totalCost = totalCost; }
    public Long getPlanRows() { return planRows; }
    public void setPlanRows(Long planRows) { this.planRows = planRows; }
    public Integer getPlanWidth() { return planWidth; }
    public void setPlanWidth(Integer planWidth) { this.planWidth = planWidth; }
    public Double getActualStartupTime() { return actualStartupTime; }
    public void setActualStartupTime(Double actualStartupTime) { this.actualStartupTime = actualStartupTime; }
    public Double getActualTotalTime() { return actualTotalTime; }
    public void setActualTotalTime(Double actualTotalTime) { this.actualTotalTime = actualTotalTime; }
    public Long getActualRows() { return actualRows; }
    public void setActualRows(Long actualRows) { this.actualRows = actualRows; }
    public Long getActualLoops() { return actualLoops; }
    public void setActualLoops(Long actualLoops) { this.actualLoops = actualLoops; }
    public Double getExclusiveTimeMs() { return exclusiveTimeMs; }
    public void setExclusiveTimeMs(Double exclusiveTimeMs) { this.exclusiveTimeMs = exclusiveTimeMs; }
    public Double getCostPercentage() { return costPercentage; }
    public void setCostPercentage(Double costPercentage) { this.costPercentage = costPercentage; }
    public Double getTimePercentage() { return timePercentage; }
    public void setTimePercentage(Double timePercentage) { this.timePercentage = timePercentage; }
    public String getFilter() { return filter; }
    public void setFilter(String filter) { this.filter = filter; }
    public Long getRowsRemovedByFilter() { return rowsRemovedByFilter; }
    public void setRowsRemovedByFilter(Long rowsRemovedByFilter) { this.rowsRemovedByFilter = rowsRemovedByFilter; }
    public String getIndexName() { return indexName; }
    public void setIndexName(String indexName) { this.indexName = indexName; }
    public String getIndexCond() { return indexCond; }
    public void setIndexCond(String indexCond) { this.indexCond = indexCond; }
    public String getScanDirection() { return scanDirection; }
    public void setScanDirection(String scanDirection) { this.scanDirection = scanDirection; }
    public String getJoinType() { return joinType; }
    public void setJoinType(String joinType) { this.joinType = joinType; }
    public String getHashCond() { return hashCond; }
    public void setHashCond(String hashCond) { this.hashCond = hashCond; }
    public String getSortKey() { return sortKey; }
    public void setSortKey(String sortKey) { this.sortKey = sortKey; }
    public String getSortMethod() { return sortMethod; }
    public void setSortMethod(String sortMethod) { this.sortMethod = sortMethod; }
    public String getSortSpaceUsed() { return sortSpaceUsed; }
    public void setSortSpaceUsed(String sortSpaceUsed) { this.sortSpaceUsed = sortSpaceUsed; }
    public String getSortSpaceType() { return sortSpaceType; }
    public void setSortSpaceType(String sortSpaceType) { this.sortSpaceType = sortSpaceType; }
    public Long getSharedHitBlocks() { return sharedHitBlocks; }
    public void setSharedHitBlocks(Long sharedHitBlocks) { this.sharedHitBlocks = sharedHitBlocks; }
    public Long getSharedReadBlocks() { return sharedReadBlocks; }
    public void setSharedReadBlocks(Long sharedReadBlocks) { this.sharedReadBlocks = sharedReadBlocks; }
    public Long getSharedDirtiedBlocks() { return sharedDirtiedBlocks; }
    public void setSharedDirtiedBlocks(Long sharedDirtiedBlocks) { this.sharedDirtiedBlocks = sharedDirtiedBlocks; }
    public Long getSharedWrittenBlocks() { return sharedWrittenBlocks; }
    public void setSharedWrittenBlocks(Long sharedWrittenBlocks) { this.sharedWrittenBlocks = sharedWrittenBlocks; }
    public List<PlanNodeDto> getPlans() { return plans; }
    public void setPlans(List<PlanNodeDto> plans) { this.plans = plans; }
}
