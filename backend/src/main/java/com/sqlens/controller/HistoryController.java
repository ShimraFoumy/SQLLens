package com.sqlens.controller;

import com.sqlens.dto.QueryHistoryItemDto;
import com.sqlens.service.QueryHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/history")
public class HistoryController {

    private final QueryHistoryService queryHistoryService;

    public HistoryController(QueryHistoryService queryHistoryService) {
        this.queryHistoryService = queryHistoryService;
    }

    @GetMapping
    public ResponseEntity<List<QueryHistoryItemDto>> getHistory() {
        return ResponseEntity.ok(queryHistoryService.getHistory());
    }

    @DeleteMapping
    public ResponseEntity<Void> clearHistory() {
        queryHistoryService.clearHistory();
        return ResponseEntity.noContent().build();
    }
}
