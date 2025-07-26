package com.lumen.docgen.model;

import java.util.List;

public class DocumentMergeRequest {
    private List<String> filePaths;
    private String outputPath;
    private boolean createTableOfContents;
    private String tableOfContentsTitle;
    private boolean addPageBreaks;
    private boolean preserveFormatting;

    // Constructors
    public DocumentMergeRequest() {}

    public DocumentMergeRequest(List<String> filePaths, String outputPath) {
        this.filePaths = filePaths;
        this.outputPath = outputPath;
        this.createTableOfContents = false;
        this.addPageBreaks = true;
        this.preserveFormatting = true;
    }

    // Getters and Setters
    public List<String> getFilePaths() { return filePaths; }
    public void setFilePaths(List<String> filePaths) { this.filePaths = filePaths; }
    
    public String getOutputPath() { return outputPath; }
    public void setOutputPath(String outputPath) { this.outputPath = outputPath; }
    
    public boolean isCreateTableOfContents() { return createTableOfContents; }
    public void setCreateTableOfContents(boolean createTableOfContents) { this.createTableOfContents = createTableOfContents; }
    
    public String getTableOfContentsTitle() { return tableOfContentsTitle; }
    public void setTableOfContentsTitle(String tableOfContentsTitle) { this.tableOfContentsTitle = tableOfContentsTitle; }
    
    public boolean isAddPageBreaks() { return addPageBreaks; }
    public void setAddPageBreaks(boolean addPageBreaks) { this.addPageBreaks = addPageBreaks; }
    
    public boolean isPreserveFormatting() { return preserveFormatting; }
    public void setPreserveFormatting(boolean preserveFormatting) { this.preserveFormatting = preserveFormatting; }
}