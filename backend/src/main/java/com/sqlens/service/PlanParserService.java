package com.sqlens.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sqlens.dto.ExplainResponseDto;
import com.sqlens.dto.PerformanceSummaryDto;
import com.sqlens.dto.PlanNodeDto;
import com.sqlens.exception.QueryExecutionException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class PlanParserService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ExplainResponseDto parseExplainJson(String explainJson, String rawText) {
        try {
            JsonNode rootArray = objectMapper.readTree(explainJson);
            if (!rootArray.isArray() || rootArray.isEmpty()) {
                throw new QueryExecutionException("Invalid EXPLAIN JSON output from PostgreSQL.");
            }

            JsonNode queryObj = rootArray.get(0);
            double planningTime = queryObj.has("Planning Time") ? queryObj.get("Planning Time").asDouble() : 0.0;
            double executionTime = queryObj.has("Execution Time") ? queryObj.get("Execution Time").asDouble() : 0.0;

            JsonNode planNode = queryObj.get("Plan");
            if (planNode == null) {
                throw new QueryExecutionException("Missing 'Plan' root node in PostgreSQL EXPLAIN output.");
            }

            AtomicInteger idCounter = new AtomicInteger(0);
            PlanNodeDto rootDto = parseNode(planNode, idCounter);

            // Compute total cost and percentages
            double rootTotalCost = rootDto.getTotalCost() != null ? rootDto.getTotalCost() : 1.0;
            double rootActualTime = rootDto.getActualTotalTime() != null ? rootDto.getActualTotalTime() : (executionTime > 0 ? executionTime : 1.0);
            calculateDerivedMetrics(rootDto, rootTotalCost, rootActualTime);

            // Build performance summary
            PerformanceSummaryDto summary = buildSummary(rootDto, planningTime, executionTime);

            return ExplainResponseDto.builder()
                    .plan(rootDto)
                    .summary(summary)
                    .rawJson(explainJson)
                    .rawText(rawText)
                    .build();

        } catch (Exception e) {
            if (e instanceof QueryExecutionException) {
                throw (QueryExecutionException) e;
            }
            throw new QueryExecutionException("Failed to parse PostgreSQL execution plan: " + e.getMessage(), e);
        }
    }

    private PlanNodeDto parseNode(JsonNode node, AtomicInteger idCounter) {
        PlanNodeDto dto = PlanNodeDto.builder()
                .id("node-" + idCounter.getAndIncrement())
                .nodeType(getString(node, "Node Type"))
                .relationName(getString(node, "Relation Name"))
                .schema(getString(node, "Schema"))
                .alias(getString(node, "Alias"))
                .startupCost(getDouble(node, "Startup Cost"))
                .totalCost(getDouble(node, "Total Cost"))
                .planRows(getLong(node, "Plan Rows"))
                .planWidth(getInt(node, "Plan Width"))
                .actualStartupTime(getDouble(node, "Actual Startup Time"))
                .actualTotalTime(getDouble(node, "Actual Total Time"))
                .actualRows(getLong(node, "Actual Rows"))
                .actualLoops(getLong(node, "Actual Loops"))
                .filter(getString(node, "Filter"))
                .rowsRemovedByFilter(getLong(node, "Rows Removed by Filter"))
                .indexName(getString(node, "Index Name"))
                .indexCond(getString(node, "Index Cond"))
                .scanDirection(getString(node, "Scan Direction"))
                .joinType(getString(node, "Join Type"))
                .hashCond(getString(node, "Hash Cond"))
                .sortKey(getString(node, "Sort Key"))
                .sortMethod(getString(node, "Sort Method"))
                .sortSpaceUsed(getString(node, "Sort Space Used"))
                .sortSpaceType(getString(node, "Sort Space Type"))
                .sharedHitBlocks(getLong(node, "Shared Hit Blocks"))
                .sharedReadBlocks(getLong(node, "Shared Read Blocks"))
                .sharedDirtiedBlocks(getLong(node, "Shared Dirtied Blocks"))
                .sharedWrittenBlocks(getLong(node, "Shared Written Blocks"))
                .plans(new ArrayList<>())
                .build();

        if (node.has("Plans") && node.get("Plans").isArray()) {
            for (JsonNode childNode : node.get("Plans")) {
                dto.getPlans().add(parseNode(childNode, idCounter));
            }
        }

        return dto;
    }

    private void calculateDerivedMetrics(PlanNodeDto node, double rootTotalCost, double rootActualTime) {
        // Exclusive time calculation
        double childrenTime = 0.0;
        for (PlanNodeDto child : node.getPlans()) {
            calculateDerivedMetrics(child, rootTotalCost, rootActualTime);
            if (child.getActualTotalTime() != null) {
                childrenTime += child.getActualTotalTime();
            }
        }

        double nodeTime = node.getActualTotalTime() != null ? node.getActualTotalTime() : 0.0;
        double exclusive = Math.max(0.0, nodeTime - childrenTime);
        node.setExclusiveTimeMs(Math.round(exclusive * 1000.0) / 1000.0);

        if (rootTotalCost > 0 && node.getTotalCost() != null) {
            node.setCostPercentage(Math.round((node.getTotalCost() / rootTotalCost * 100.0) * 10.0) / 10.0);
        } else {
            node.setCostPercentage(0.0);
        }

        if (rootActualTime > 0) {
            node.setTimePercentage(Math.round((exclusive / rootActualTime * 100.0) * 10.0) / 10.0);
        } else {
            node.setTimePercentage(0.0);
        }
    }

    private PerformanceSummaryDto buildSummary(PlanNodeDto root, double planningTime, double executionTime) {
        List<PlanNodeDto> allNodes = new ArrayList<>();
        collectAllNodes(root, allNodes);

        int totalOperations = allNodes.size();
        long totalScanned = 0;
        long totalFiltered = 0;
        long totalHits = 0;
        long totalReads = 0;
        boolean usesIndex = false;

        PlanNodeDto bottleneck = root;
        double maxExclusiveTime = -1.0;
        String primaryScanType = "None";

        for (PlanNodeDto n : allNodes) {
            if (n.getActualRows() != null) {
                totalScanned += n.getActualRows();
            }
            if (n.getRowsRemovedByFilter() != null) {
                totalFiltered += n.getRowsRemovedByFilter();
            }
            if (n.getSharedHitBlocks() != null) {
                totalHits += n.getSharedHitBlocks();
            }
            if (n.getSharedReadBlocks() != null) {
                totalReads += n.getSharedReadBlocks();
            }
            if (n.getNodeType() != null && n.getNodeType().toLowerCase().contains("index")) {
                usesIndex = true;
            }
            if (n.getNodeType() != null && n.getNodeType().toLowerCase().contains("scan")) {
                primaryScanType = n.getNodeType();
            }
            if (n.getExclusiveTimeMs() != null && n.getExclusiveTimeMs() > maxExclusiveTime) {
                maxExclusiveTime = n.getExclusiveTimeMs();
                bottleneck = n;
            }
        }

        double totalBlocks = totalHits + totalReads;
        double hitRatio = totalBlocks > 0 ? (double) totalHits / totalBlocks * 100.0 : 100.0;

        return PerformanceSummaryDto.builder()
                .planningTimeMs(planningTime)
                .executionTimeMs(executionTime > 0 ? executionTime : (root.getActualTotalTime() != null ? root.getActualTotalTime() : 0.0))
                .totalCost(root.getTotalCost() != null ? root.getTotalCost() : 0.0)
                .rowsReturned(root.getActualRows() != null ? root.getActualRows() : 0)
                .totalOperations(totalOperations)
                .primaryOperation(root.getNodeType())
                .primaryScanType(primaryScanType)
                .bottleneckNode(bottleneck.getNodeType() + (bottleneck.getRelationName() != null ? " on " + bottleneck.getRelationName() : ""))
                .bottleneckTimeMs(maxExclusiveTime >= 0 ? maxExclusiveTime : 0.0)
                .totalRowsScanned(totalScanned)
                .totalRowsRemovedByFilter(totalFiltered)
                .bufferHitRatio(Math.round(hitRatio * 10.0) / 10.0)
                .usesIndex(usesIndex)
                .build();
    }

    private void collectAllNodes(PlanNodeDto current, List<PlanNodeDto> accumulator) {
        accumulator.add(current);
        for (PlanNodeDto child : current.getPlans()) {
            collectAllNodes(child, accumulator);
        }
    }

    private String getString(JsonNode node, String field) {
        return node.has(field) && !node.get(field).isNull() ? node.get(field).asText() : null;
    }

    private Double getDouble(JsonNode node, String field) {
        return node.has(field) && !node.get(field).isNull() ? node.get(field).asDouble() : null;
    }

    private Long getLong(JsonNode node, String field) {
        return node.has(field) && !node.get(field).isNull() ? node.get(field).asLong() : null;
    }

    private Integer getInt(JsonNode node, String field) {
        return node.has(field) && !node.get(field).isNull() ? node.get(field).asInt() : null;
    }
}
