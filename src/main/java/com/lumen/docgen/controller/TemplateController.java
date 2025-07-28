package com.lumen.docgen.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lumen.docgen.model.FileInfo;
import com.lumen.docgen.service.TemplateFileService;

@RestController
@RequestMapping("/api/templates")
public class TemplateController {
    
    private final TemplateFileService templateFileService;

    public TemplateController(TemplateFileService templateFileService) {
        this.templateFileService = templateFileService;
    }

    @GetMapping("/structure")
    public ResponseEntity<List<FileInfo>> getTemplateStructure() {
        return ResponseEntity.ok(templateFileService.getTemplateStructure());
    }
}