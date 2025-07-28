package com.lumen.docgen.model;

import java.time.LocalDateTime;
import java.util.List;

public class FileInfo {
    private String name;
    private boolean isDirectory;
    private LocalDateTime lastModified;
    private List<FileInfo> children;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public List<FileInfo> getChildren() {
        return children;
    }

    public void setChildren(List<FileInfo> children) {
        this.children = children;
    }
}