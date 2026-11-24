package com.sqlens.service;

import com.sqlens.dto.ExplainResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlanParserServiceTest {

    private PlanParserService planParserService;

    @BeforeEach
    void setUp() {
        planParserService = new PlanParserService();
    }

    @Test
    void shouldParseExplainJsonSuccessfully() {
        String mockPostgresExplainJson = """
                [
                  {
                    "Plan": {
                      "Node Type": "Hash Join",
                      "Parallel Aware": false,
                      "Join Type": "Inner",
                      "Startup Cost": 250.00,
                      "Total Cost": 1250.50,
                      "Plan Rows": 1200,
                      "Plan Width": 64,
                      "Actual Startup Time": 1.200,
                      "Actual Total Time": 8.450,
                      "Actual Rows": 1150,
                      "Actual Loops": 1,
                      "Hash Cond": "(o.customer_id = c.id)",
                      "Shared Hit Blocks": 142,
                      "Shared Read Blocks": 12,
                      "Plans": [
                        {
                          "Node Type": "Seq Scan",
                          "Parent Relationship": "Outer",
                          "Relation Name": "orders",
                          "Alias": "o",
                          "Startup Cost": 0.00,
                          "Total Cost": 850.00,
                          "Plan Rows": 50000,
                          "Actual Startup Time": 0.015,
                          "Actual Total Time": 4.100,
                          "Actual Rows": 50000,
                          "Actual Loops": 1
                        },
                        {
                          "Node Type": "Hash",
                          "Parent Relationship": "Inner",
                          "Startup Cost": 150.00,
                          "Total Cost": 150.00,
                          "Plan Rows": 5000,
                          "Actual Startup Time": 0.850,
                          "Actual Total Time": 0.850,
                          "Actual Rows": 5000,
                          "Actual Loops": 1,
                          "Plans": [
                            {
                              "Node Type": "Seq Scan",
                              "Parent Relationship": "Outer",
                              "Relation Name": "customers",
                              "Alias": "c",
                              "Startup Cost": 0.00,
                              "Total Cost": 150.00,
                              "Plan Rows": 5000,
                              "Actual Startup Time": 0.010,
                              "Actual Total Time": 0.620,
                              "Actual Rows": 5000,
                              "Actual Loops": 1
                            }
                          ]
                        }
                      ]
                    },
                    "Planning Time": 0.450,
                    "Execution Time": 9.120
                  }
                ]
                """;

        ExplainResponseDto response = planParserService.parseExplainJson(mockPostgresExplainJson, "Mock Raw Text Plan");

        assertNotNull(response);
        assertNotNull(response.getPlan());
        assertEquals("Hash Join", response.getPlan().getNodeType());
        assertEquals(1250.50, response.getPlan().getTotalCost());
        assertEquals(8.450, response.getPlan().getActualTotalTime());
        assertEquals(2, response.getPlan().getPlans().size());

        // Verify summary
        assertNotNull(response.getSummary());
        assertEquals(0.450, response.getSummary().getPlanningTimeMs());
        assertEquals(9.120, response.getSummary().getExecutionTimeMs());
        assertEquals(1150, response.getSummary().getRowsReturned());
        assertTrue(response.getSummary().getTotalOperations() >= 3);
    }
}
