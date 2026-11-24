package com.sqlens.controller;

import com.sqlens.dto.SampleQueryDto;
import com.sqlens.dto.TableMetadataDto;
import com.sqlens.service.SchemaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/schema")
public class SchemaController {

    private final SchemaService schemaService;

    public SchemaController(SchemaService schemaService) {
        this.schemaService = schemaService;
    }

    @GetMapping("/tables")
    public ResponseEntity<List<TableMetadataDto>> getTables() {
        return ResponseEntity.ok(schemaService.getTables());
    }

    @GetMapping("/sample-queries")
    public ResponseEntity<List<SampleQueryDto>> getSampleQueries() {
        return ResponseEntity.ok(schemaService.getSampleQueries());
    }
}
