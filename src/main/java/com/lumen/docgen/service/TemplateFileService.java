package com.lumen.docgen.service;

import com.lumen.docgen.dto.FileSystemItemDTO;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class TemplateFileService {

    private final String templateRootPath = "path/to/your/templates"; // Configure this path

    public List<FileSystemItemDTO> getAvailableTemplates() {
        File rootDir = new File(templateRootPath);
        return getFileSystemItems(rootDir);
    }

    private List<FileSystemItemDTO> getFileSystemItems(File directory) {
        List<FileSystemItemDTO> items = new ArrayList<>();
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                try {
                    FileSystemItemDTO item = new FileSystemItemDTO();
                    item.setName(file.getName());
                    item.setPath(file.getPath());
                    item.setDirectory(file.isDirectory());

                    BasicFileAttributes attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
                    LocalDateTime lastModified = LocalDateTime.ofInstant(
                        attrs.lastModifiedTime().toInstant(),
                        ZoneId.systemDefault()
                    );
                    item.setLastModified(lastModified);

                    if (file.isDirectory()) {
                        item.setChildren(getFileSystemItems(file));
                    }

                    items.add(item);
                } catch (Exception e) {
                    // Handle or log the exception appropriately
                }
            }
        }
        return items;
    }
}