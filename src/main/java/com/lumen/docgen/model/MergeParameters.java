package com.lumen.docgen.model;

public class MergeParameters {
    private String baseDocumentPath;
    private String documentToMergePath;
    private String outputPath;
    private MergePosition position;
    private boolean addPageBreak;
    private boolean preserveStyles;
    private String sectionTitle;

    public enum MergePosition {
        BEGINNING, END, AFTER_TOC, CUSTOM_POSITION
    }

    // Constructors
    public MergeParameters() {}

    public MergeParameters(String baseDocument, String documentToMerge, String output) {
        this.baseDocumentPath = baseDocument;
        this.documentToMergePath = documentToMerge;
        this.outputPath = output;
        this.position = MergePosition.END;
        this.addPageBreak = true;
        this.preserveStyles = true;
    }

    // Getters and Setters
    public String getBaseDocumentPath() { return baseDocumentPath; }
    public void setBaseDocumentPath(String baseDocumentPath) { this.baseDocumentPath = baseDocumentPath; }
    
    public String getDocumentToMergePath() { return documentToMergePath; }
    public void setDocumentToMergePath(String documentToMergePath) { this.documentToMergePath = documentToMergePath; }
    
    public String getOutputPath() { return outputPath; }
    public void setOutputPath(String outputPath) { this.outputPath = outputPath; }
    
    public MergePosition getPosition() { return position; }
    public void setPosition(MergePosition position) { this.position = position; }
    
    public boolean isAddPageBreak() { return addPageBreak; }
    public void setAddPageBreak(boolean addPageBreak) { this.addPageBreak = addPageBreak; }
    
    public boolean isPreserveStyles() { return preserveStyles; }
    public void setPreserveStyles(boolean preserveStyles) { this.preserveStyles = preserveStyles; }
    
    public String getSectionTitle() { return sectionTitle; }
    public void setSectionTitle(String sectionTitle) { this.sectionTitle = sectionTitle; }
}