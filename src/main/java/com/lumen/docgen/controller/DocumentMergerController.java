package com.lumen.docgen.controller;


import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lumen.docgen.model.DocumentMergeRequest;
import com.lumen.docgen.model.MergeParameters;
import com.lumen.docgen.service.DocumentMergerService;

@RestController
@RequestMapping("/api/documents")
public class DocumentMergerController {
    
    private static final Logger logger = LoggerFactory.getLogger(DocumentMergerController.class);
    
    @Autowired
    private DocumentMergerService documentMergerService;
    
    @PostMapping("/merge")
    public ResponseEntity<Map<String, Object>> mergeDocuments(@RequestBody DocumentMergeRequest request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String outputPath = documentMergerService.mergeDocuments(request);
            response.put("success", true);
            response.put("message", "Documents merged successfully");
            response.put("outputPath", outputPath);
            response.put("documentsCount", request.getFilePaths().size());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error merging documents: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error merging documents: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/merge-with-parameters")
    public ResponseEntity<Map<String, Object>> mergeWithParameters(@RequestBody MergeParameters params) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String outputPath = documentMergerService.mergeWithParameters(params);
            response.put("success", true);
            response.put("message", "Documents merged with parameters successfully");
            response.put("outputPath", outputPath);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error merging documents with parameters: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error merging documents: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/create-toc")
    public ResponseEntity<Map<String, Object>> createTableOfContents(
            @RequestParam String documentPath,
            @RequestParam(defaultValue = "Table of Contents") String title) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            org.docx4j.openpackaging.packages.WordprocessingMLPackage doc = 
                org.docx4j.openpackaging.packages.WordprocessingMLPackage.load(
                    new java.io.FileInputStream(documentPath));
            
            documentMergerService.createTableOfContents(doc, title);
            
            doc.save(new java.io.FileOutputStream(documentPath));
            
            response.put("success", true);
            response.put("message", "Table of contents created successfully");
            response.put("documentPath", documentPath);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error creating table of contents: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Error creating table of contents: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}