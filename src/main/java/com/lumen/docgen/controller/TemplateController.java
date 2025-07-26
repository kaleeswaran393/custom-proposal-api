package com.lumen.docgen.controller;
import com.lumen.docgen.dto.FileSystemItemDTO;
import com.lumen.docgen.service.TemplateFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/templates")
public class TemplateController {

    @Autowired
    private TemplateFileService templateFileService;

    @GetMapping("/available")
    public ResponseEntity<List<FileSystemItemDTO>> getAvailableTemplates() {
        List<FileSystemItemDTO> templates = templateFileService.getAvailableTemplates();
        return ResponseEntity.ok(templates);
    }
}