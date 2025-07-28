package com.lumen.docgen.service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.lumen.docgen.config.TemplateConfig;
import com.lumen.docgen.model.FileInfo;

@Service
public class TemplateFileService {
    
    private final TemplateConfig templateConfig;

    public TemplateFileService(TemplateConfig templateConfig) {
        this.templateConfig = templateConfig;
    }

    public List<FileInfo> getTemplateStructure() {
        File rootDir = new File(templateConfig.getPath());
        if (!rootDir.exists() || !rootDir.isDirectory()) {
            throw new RuntimeException("Template directory not found: " + templateConfig.getPath());
        }
        return getFileInfoList(rootDir);
    }

    private List<FileInfo> getFileInfoList(File directory) {
        List<FileInfo> fileInfoList = new ArrayList<>();
        File[] files = directory.listFiles();
        
        if (files != null) {
            for (File file : files) {
                FileInfo fileInfo = new FileInfo();
                fileInfo.setName(file.getName());
                fileInfo.setDirectory(file.isDirectory());
                try {
                    fileInfo.setLastModified(LocalDateTime.ofInstant(
                        Files.getLastModifiedTime(Path.of(file.getPath())).toInstant(), 
                        ZoneId.systemDefault()
                    ));
                } catch (java.io.IOException e) {
                    fileInfo.setLastModified(null);
                }

                if (file.isDirectory()) {
                    fileInfo.setChildren(getFileInfoList(file));
                }
                
                fileInfoList.add(fileInfo);
            }
        }
        
        return fileInfoList;
    }
}