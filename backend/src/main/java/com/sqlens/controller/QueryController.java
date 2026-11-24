package com.sqlens.controller;

import com.sqlens.dto.*;
import com.sqlens.service.QueryExecutionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/query")
public class QueryController {

    private final QueryExecutionService queryExecutionService;

    public QueryController(QueryExecutionService queryExecutionService) {
        this.queryExecutionService = queryExecutionService;
    }

    @PostMapping("/execute")
    public ResponseEntity<QueryResultResponse> executeQuery(@RequestBody ExecuteQueryRequest request) {
        QueryResultResponse response = queryExecutionService.executeQuery(request.getQuery());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/explain")
    public ResponseEntity<ExplainResponseDto> explainQuery(@RequestBody ExecuteQueryRequest request) {
        ExplainResponseDto response = queryExecutionService.explainQuery(request.getQuery(), request.isAnalyze());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/compare")
    public ResponseEntity<CompareQueriesResponse> compareQueries(@RequestBody CompareQueriesRequest request) {
        CompareQueriesResponse response = queryExecutionService.compareQueries(request.getQueryA(), request.getQueryB());
        return ResponseEntity.ok(response);
    }
}
