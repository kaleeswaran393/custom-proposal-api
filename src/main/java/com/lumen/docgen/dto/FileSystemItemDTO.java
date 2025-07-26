package com.lumen.docgen.dto;

import java.time.LocalDateTime;
import java.util.List;

public class FileSystemItemDTO {
    private String name;
    private String path;
    private boolean isDirectory;
    private LocalDateTime lastModified;
    private List<FileSystemItemDTO> children;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setDirectory(boolean directory) {
        isDirectory = directory;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

    public List<FileSystemItemDTO> getChildren() {
        return children;
    }

    public void setChildren(List<FileSystemItemDTO> children) {
        this.children = children;
    }
}