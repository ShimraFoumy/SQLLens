package com.sqlens.service;

import com.sqlens.dto.*;
import com.sqlens.exception.QueryExecutionException;
import com.sqlens.security.SqlSecurityValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class QueryExecutionService {

    private static final Logger log = LoggerFactory.getLogger(QueryExecutionService.class);

    private final JdbcTemplate jdbcTemplate;
    private final SqlSecurityValidator securityValidator;
    private final PlanParserService planParserService;
    private final OptimizationAdvisorService optimizationAdvisorService;
    private final QueryHistoryService queryHistoryService;

    @Value("${sqllens.query.timeout-seconds:5}")
    private int timeoutSeconds;

    @Value("${sqllens.query.max-rows:500}")
    private int maxRows;

    public QueryExecutionService(JdbcTemplate jdbcTemplate,
                                 SqlSecurityValidator securityValidator,
                                 PlanParserService planParserService,
                                 OptimizationAdvisorService optimizationAdvisorService,
                                 QueryHistoryService queryHistoryService) {
        this.jdbcTemplate = jdbcTemplate;
        this.securityValidator = securityValidator;
        this.planParserService = planParserService;
        this.optimizationAdvisorService = optimizationAdvisorService;
        this.queryHistoryService = queryHistoryService;
    }

    /**
     * Executes a SELECT query safely with timeout and pagination cap.
     */
    public QueryResultResponse executeQuery(String rawSql) {
        String sanitizedSql = securityValidator.validateAndSanitize(rawSql);
        long startTime = System.nanoTime();

        try {
            return jdbcTemplate.execute((Statement stmt) -> {
                stmt.setQueryTimeout(timeoutSeconds);
                stmt.setMaxRows(maxRows + 1);

                try (ResultSet rs = stmt.executeQuery(sanitizedSql)) {
                    long endTime = System.nanoTime();
                    double durationMs = (endTime - startTime) / 1_000_000.0;
                    durationMs = Math.round(durationMs * 100.0) / 100.0;

                    ResultSetMetaData meta = rs.getMetaData();
                    int colCount = meta.getColumnCount();
                    List<ColumnMetadata> columns = new ArrayList<>();
                    for (int i = 1; i <= colCount; i++) {
                        columns.add(ColumnMetadata.builder()
                                .name(meta.getColumnLabel(i))
                                .typeName(meta.getColumnTypeName(i))
                                .typeCode(meta.getColumnType(i))
                                .build());
                    }

                    List<Map<String, Object>> rows = new ArrayList<>();
                    boolean truncated = false;

                    while (rs.next()) {
                        if (rows.size() >= maxRows) {
                            truncated = true;
                            break;
                        }
                        Map<String, Object> row = new LinkedHashMap<>();
                        for (int i = 1; i <= colCount; i++) {
                            row.put(meta.getColumnLabel(i), rs.getObject(i));
                        }
                        rows.add(row);
                    }

                    queryHistoryService.addHistory(QueryHistoryItemDto.builder()
                            .query(sanitizedSql)
                            .executionTimeMs(durationMs)
                            .rowsReturned(rows.size())
                            .status("SUCCESS")
                            .primaryOperation("SELECT")
                            .timestamp(LocalDateTime.now())
                            .build());

                    return QueryResultResponse.builder()
                            .executionTimeMs(durationMs)
                            .rowsReturned(rows.size())
                            .columns(columns)
                            .rows(rows)
                            .truncated(truncated)
                            .status("SUCCESS")
                            .message(truncated ? "Results capped at maximum " + maxRows + " rows." : "Query executed successfully.")
                            .build();
                }
            });
        } catch (Exception e) {
            double durationMs = (System.nanoTime() - startTime) / 1_000_000.0;
            queryHistoryService.addHistory(QueryHistoryItemDto.builder()
                    .query(sanitizedSql)
                    .executionTimeMs(durationMs)
                    .rowsReturned(0)
                    .status("ERROR")
                    .errorMessage(e.getMessage())
                    .timestamp(LocalDateTime.now())
                    .build());

            throw new QueryExecutionException("Database execution failed: " + e.getMessage(), e);
        }
    }

    /**
     * Executes EXPLAIN (ANALYZE, COSTS, VERBOSE, BUFFERS, FORMAT JSON)
     */
    public ExplainResponseDto explainQuery(String rawSql, boolean analyze) {
        String sanitizedSql = securityValidator.validateAndSanitize(rawSql);

        String jsonExplainSql = (analyze ? "EXPLAIN (ANALYZE, COSTS, VERBOSE, BUFFERS, FORMAT JSON) " : "EXPLAIN (COSTS, VERBOSE, FORMAT JSON) ") + sanitizedSql;
        String textExplainSql = (analyze ? "EXPLAIN (ANALYZE, COSTS, BUFFERS) " : "EXPLAIN (COSTS) ") + sanitizedSql;

        try {
            // 1. Fetch JSON plan
            String jsonOutput = jdbcTemplate.queryForObject(jsonExplainSql, String.class);

            // 2. Fetch Text plan for human-readable tab
            List<String> textLines = jdbcTemplate.query(textExplainSql, (rs, rowNum) -> rs.getString(1));
            String textOutput = String.join("\n", textLines);

            // 3. Parse JSON into DTO tree & summary
            ExplainResponseDto response = planParserService.parseExplainJson(jsonOutput, textOutput);

            // 4. Generate optimization tips
            List<OptimizationTipDto> tips = optimizationAdvisorService.generateTips(response.getPlan());
            response.setOptimizationTips(tips);

            // Record to history
            queryHistoryService.addHistory(QueryHistoryItemDto.builder()
                    .query(sanitizedSql)
                    .executionTimeMs(response.getSummary().getExecutionTimeMs())
                    .planningTimeMs(response.getSummary().getPlanningTimeMs())
                    .rowsReturned(response.getSummary().getRowsReturned())
                    .status("SUCCESS")
                    .primaryOperation(response.getSummary().getPrimaryOperation())
                    .timestamp(LocalDateTime.now())
                    .build());

            return response;
        } catch (Exception e) {
            log.error("EXPLAIN execution failed for query: {}", sanitizedSql, e);
            throw new QueryExecutionException("EXPLAIN command failed: " + e.getMessage(), e);
        }
    }

    /**
     * Compares two queries side-by-side.
     */
    public CompareQueriesResponse compareQueries(String rawSqlA, String rawSqlB) {
        ExplainResponseDto planA = explainQuery(rawSqlA, true);
        ExplainResponseDto planB = explainQuery(rawSqlB, true);

        double timeA = planA.getSummary().getExecutionTimeMs();
        double timeB = planB.getSummary().getExecutionTimeMs();
        double costA = planA.getSummary().getTotalCost();
        double costB = planB.getSummary().getTotalCost();

        double timeDiff = Math.round(Math.abs(timeA - timeB) * 100.0) / 100.0;
        double costDiff = Math.round(Math.abs(costA - costB) * 100.0) / 100.0;

        String faster = "TIE";
        double speedup = 1.0;
        if (timeA < timeB && timeA > 0) {
            faster = "QUERY_A";
            speedup = Math.round((timeB / timeA) * 10.0) / 10.0;
        } else if (timeB < timeA && timeB > 0) {
            faster = "QUERY_B";
            speedup = Math.round((timeA / timeB) * 10.0) / 10.0;
        }

        String summary = String.format("%s was faster by %.2f ms (%.1fx faster). Cost difference: %.2f.",
                "QUERY_A".equals(faster) ? "Query A" : ("QUERY_B".equals(faster) ? "Query B" : "Neither query"),
                timeDiff, speedup, costDiff);

        return CompareQueriesResponse.builder()
                .planA(planA)
                .planB(planB)
                .executionTimeDiffMs(timeDiff)
                .costDiff(costDiff)
                .fasterQuery(faster)
                .speedupFactor(speedup)
                .comparisonSummary(summary)
                .build();
    }
}
