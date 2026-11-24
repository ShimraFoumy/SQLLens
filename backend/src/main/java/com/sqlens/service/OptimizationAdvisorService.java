package com.sqlens.service;

import com.sqlens.dto.OptimizationTipDto;
import com.sqlens.dto.PlanNodeDto;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OptimizationAdvisorService {

    public List<OptimizationTipDto> generateTips(PlanNodeDto rootNode) {
        List<OptimizationTipDto> tips = new ArrayList<>();
        inspectNode(rootNode, tips);

        if (tips.isEmpty()) {
            tips.add(OptimizationTipDto.builder()
                    .level("SUCCESS")
                    .title("Healthy Execution Plan")
                    .message("The PostgreSQL query optimizer selected a balanced execution strategy with no major bottlenecks detected.")
                    .recommendation("No immediate indexing or query rewrites needed.")
                    .affectedNode(rootNode.getNodeType())
                    .build());
        }

        return tips;
    }

    private void inspectNode(PlanNodeDto node, List<OptimizationTipDto> tips) {
        if (node == null) return;

        String nodeType = node.getNodeType() != null ? node.getNodeType() : "";
        String relation = node.getRelationName() != null ? node.getRelationName() : "";

        // 1. Sequential Scan on large table with heavy filtering
        if ("Seq Scan".equalsIgnoreCase(nodeType)) {
            long removed = node.getRowsRemovedByFilter() != null ? node.getRowsRemovedByFilter() : 0;
            long actual = node.getActualRows() != null ? node.getActualRows() : 0;

            if (removed > 1000 || (actual > 1000 && node.getFilter() != null)) {
                tips.add(OptimizationTipDto.builder()
                        .level("WARNING")
                        .title("Sequential Scan on Large Table (" + relation + ")")
                        .message("PostgreSQL scanned entire table '" + relation + "' and discarded " + removed +
                                " rows via filter: " + (node.getFilter() != null ? node.getFilter() : "predicate") + ".")
                        .recommendation("Consider creating an index on the column(s) evaluated in the WHERE clause: CREATE INDEX idx_" +
                                relation + "_search ON " + relation + "(<filter_column>);")
                        .affectedNode(nodeType + " (" + relation + ")")
                        .build());
            } else if (removed > 0 && node.getFilter() != null) {
                tips.add(OptimizationTipDto.builder()
                        .level("INFO")
                        .title("Full Table Scan with Filter on " + relation)
                        .message("Evaluated filter sequentially on " + (actual + removed) + " rows.")
                        .recommendation("If table grows significantly, evaluate adding a B-Tree index.")
                        .affectedNode(nodeType + " (" + relation + ")")
                        .build());
            }
        }

        // 2. Index Scan / Bitmap Index Scan usage
        if (nodeType.contains("Index Scan")) {
            tips.add(OptimizationTipDto.builder()
                    .level("SUCCESS")
                    .title("Index Utilization: " + (node.getIndexName() != null ? node.getIndexName() : "Active Index"))
                    .message("The query planner leveraged index '" + node.getIndexName() + "' on '" + relation +
                            "', avoiding a full sequential scan.")
                    .recommendation("Index is actively speeding up query retrieval.")
                    .affectedNode(nodeType + " (" + relation + ")")
                    .build());
        }

        // 3. Disk-based sort spillover
        if ("Sort".equalsIgnoreCase(nodeType)) {
            String spaceType = node.getSortSpaceType();
            String method = node.getSortMethod();
            if (spaceType != null && spaceType.toLowerCase().contains("disk")) {
                tips.add(OptimizationTipDto.builder()
                        .level("CRITICAL")
                        .title("Sort Operation Spilled to Disk")
                        .message("The Sort operation exceeded available RAM (work_mem) and spilled to disk: " +
                                method + " using " + node.getSortSpaceUsed() + ".")
                        .recommendation("Increase PostgreSQL 'work_mem' for this session or add an index matching the ORDER BY columns.")
                        .affectedNode(nodeType)
                        .build());
            }
        }

        // 4. Heavy cardinality estimation error (Planner stats out of date)
        if (node.getPlanRows() != null && node.getActualRows() != null && node.getActualRows() > 100) {
            double ratio = (double) node.getActualRows() / (double) Math.max(1, node.getPlanRows());
            if (ratio > 10.0 || ratio < 0.1) {
                tips.add(OptimizationTipDto.builder()
                        .level("WARNING")
                        .title("Planner Cardinality Mismatch on " + (relation.isEmpty() ? nodeType : relation))
                        .message("Estimated rows (" + node.getPlanRows() + ") deviates substantially from actual rows (" +
                                node.getActualRows() + "). Planner may choose suboptimal join or scan paths.")
                        .recommendation("Run 'ANALYZE " + (relation.isEmpty() ? "<table_name>" : relation) + ";' to refresh planner statistics.")
                        .affectedNode(nodeType)
                        .build());
            }
        }

        // 5. Nested loop with large row counts
        if ("Nested Loop".equalsIgnoreCase(nodeType) && node.getActualRows() != null && node.getActualRows() > 5000) {
            tips.add(OptimizationTipDto.builder()
                    .level("INFO")
                    .title("High Volume Nested Loop")
                    .message("A Nested Loop processed " + node.getActualRows() + " rows. Hash Join or Merge Join might be faster for large datasets.")
                    .recommendation("Ensure joined foreign keys have indexes to keep inner loop lookups fast.")
                    .affectedNode(nodeType)
                    .build());
        }

        // Recurse into children
        for (PlanNodeDto child : node.getPlans()) {
            inspectNode(child, tips);
        }
    }
}
