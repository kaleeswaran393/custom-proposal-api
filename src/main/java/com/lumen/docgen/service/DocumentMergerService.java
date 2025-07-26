package com.lumen.docgen.service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.docx4j.jaxb.Context;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.openpackaging.parts.WordprocessingML.NumberingDefinitionsPart;
import org.docx4j.openpackaging.parts.WordprocessingML.StyleDefinitionsPart;
import org.docx4j.wml.Br;
import org.docx4j.wml.FldChar;
import org.docx4j.wml.P;
import org.docx4j.wml.PPr;
import org.docx4j.wml.PPrBase.PStyle;
import org.docx4j.wml.R;
import org.docx4j.wml.STBrType;
import org.docx4j.wml.STFldCharType;
import org.docx4j.wml.Style;
import org.docx4j.wml.Styles;
import org.docx4j.wml.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.lumen.docgen.model.DocumentMergeRequest;
import com.lumen.docgen.model.MergeParameters;

import jakarta.xml.bind.JAXBElement;




@Service
public class DocumentMergerService {
    
    private static final Logger logger = LoggerFactory.getLogger(DocumentMergerService.class);
    
    /**
     * Main method to merge multiple Word documents
     */
    public String mergeDocuments(DocumentMergeRequest request) throws Exception {
        logger.info("Starting document merge process for {} documents", request.getFilePaths().size());
        
        if (request.getFilePaths() == null || request.getFilePaths().isEmpty()) {
            throw new IllegalArgumentException("File paths list cannot be null or empty");
        }
        
        WordprocessingMLPackage mergedDoc = null;
        
        try {
            // Load the first document as base
            String firstDocPath = request.getFilePaths().get(0);
            mergedDoc = WordprocessingMLPackage.load(new FileInputStream(firstDocPath));
            logger.info("Loaded base document: {}", firstDocPath);
            
            // Merge remaining documents
            for (int i = 1; i < request.getFilePaths().size(); i++) {
                String docPath = request.getFilePaths().get(i);
                logger.info("Merging document: {}", docPath);
                
                WordprocessingMLPackage docToMerge = WordprocessingMLPackage.load(new FileInputStream(docPath));
                mergeDocumentContent(mergedDoc, docToMerge, request.isAddPageBreaks(), request.isPreserveFormatting());
            }
            
            // Create table of contents if requested
            if (request.isCreateTableOfContents()) {
                String tocTitle = request.getTableOfContentsTitle() != null ? 
                    request.getTableOfContentsTitle() : "Table of Contents";
                createTableOfContents(mergedDoc, tocTitle);
            }
            
            // Save merged document
            mergedDoc.save(new FileOutputStream(request.getOutputPath()));
            logger.info("Merged document saved to: {}", request.getOutputPath());
            
            return request.getOutputPath();
            
        } catch (Exception e) {
            logger.error("Error during document merge: {}", e.getMessage(), e);
            throw new Exception("Failed to merge documents: " + e.getMessage(), e);
        }
    }
    
    /**
     * Method to merge content from source document into target document
     */
    private void mergeDocumentContent(WordprocessingMLPackage targetDoc, WordprocessingMLPackage sourceDoc, 
                                    boolean addPageBreak, boolean preserveFormatting) throws Exception {
        
        MainDocumentPart targetMainDoc = targetDoc.getMainDocumentPart();
        MainDocumentPart sourceMainDoc = sourceDoc.getMainDocumentPart();
        
        // Merge styles if preserving formatting
        if (preserveFormatting) {
            mergeStyles(targetDoc, sourceDoc);
            mergeNumbering(targetDoc, sourceDoc);
        }
        
        // Add page break before merging content
        if (addPageBreak) {
            addPageBreak(targetMainDoc);
        }
        
        // Get content from source document
        List<Object> sourceContent = sourceMainDoc.getContent();
        
        // Add source content to target document
        for (Object contentElement : sourceContent) {
            if (contentElement instanceof JAXBElement) {
                JAXBElement<?> jaxbElement = (JAXBElement<?>) contentElement;
                targetMainDoc.addObject(jaxbElement);
            } else {
                targetMainDoc.addObject(contentElement);
            }
        }
    }
    
    /**
     * Create Table of Contents
     */
    public void createTableOfContents(WordprocessingMLPackage wordPackage, String title) throws Exception {
        logger.info("Creating table of contents with title: {}", title);
        
        MainDocumentPart mainDocumentPart = wordPackage.getMainDocumentPart();
        
        // Create TOC title
        P tocTitle = createTOCTitle(title);
        mainDocumentPart.getContent().add(0, tocTitle);
        
        // Create TOC field
        P tocField = createTOCField();
        mainDocumentPart.getContent().add(1, tocField);
        
        // Add page break after TOC
        addPageBreak(mainDocumentPart);
        
        logger.info("Table of contents created successfully");
    }
    
    /**
     * Method to merge an existing document with another document using parameters
     */
    public String mergeWithParameters(MergeParameters params) throws Exception {
        logger.info("Merging documents with parameters - Base: {}, ToMerge: {}", 
                   params.getBaseDocumentPath(), params.getDocumentToMergePath());
        
        try {
            // Load base document
            WordprocessingMLPackage baseDoc = WordprocessingMLPackage.load(
                new FileInputStream(params.getBaseDocumentPath()));
            
            // Load document to merge
            WordprocessingMLPackage docToMerge = WordprocessingMLPackage.load(
                new FileInputStream(params.getDocumentToMergePath()));
            
            // Add section title if provided
            if (params.getSectionTitle() != null && !params.getSectionTitle().trim().isEmpty()) {
                addSectionTitle(baseDoc, params.getSectionTitle());
            }
            
            // Merge based on position
            switch (params.getPosition()) {
                case BEGINNING:
                    mergeAtBeginning(baseDoc, docToMerge, params);
                    break;
                case AFTER_TOC:
                    mergeAfterTOC(baseDoc, docToMerge, params);
                    break;
                case END:
                default:
                    mergeAtEnd(baseDoc, docToMerge, params);
                    break;
            }
            
            // Save merged document
            baseDoc.save(new FileOutputStream(params.getOutputPath()));
            logger.info("Parameterized merge completed. Output saved to: {}", params.getOutputPath());
            
            return params.getOutputPath();
            
        } catch (Exception e) {
            logger.error("Error during parameterized merge: {}", e.getMessage(), e);
            throw new Exception("Failed to merge with parameters: " + e.getMessage(), e);
        }
    }
    
    // Helper methods
    
    private void mergeStyles(WordprocessingMLPackage targetDoc, WordprocessingMLPackage sourceDoc) throws Exception {
        StyleDefinitionsPart targetStyles = targetDoc.getMainDocumentPart().getStyleDefinitionsPart();
        StyleDefinitionsPart sourceStyles = sourceDoc.getMainDocumentPart().getStyleDefinitionsPart();
        
        if (sourceStyles != null && targetStyles != null) {
            Styles sourceStylesContent = sourceStyles.getContents();
            Styles targetStylesContent = targetStyles.getContents();
            
            // Merge styles from source to target
            for (Style sourceStyle : sourceStylesContent.getStyle()) {
                if (!styleExists(targetStylesContent, sourceStyle.getStyleId())) {
                    targetStylesContent.getStyle().add(sourceStyle);
                }
            }
        }
    }
    
    private boolean styleExists(Styles styles, String styleId) {
        return styles.getStyle().stream()
                .anyMatch(style -> styleId.equals(style.getStyleId()));
    }
    
    private void mergeNumbering(WordprocessingMLPackage targetDoc, WordprocessingMLPackage sourceDoc) throws Exception {
        NumberingDefinitionsPart targetNumbering = targetDoc.getMainDocumentPart().getNumberingDefinitionsPart();
        NumberingDefinitionsPart sourceNumbering = sourceDoc.getMainDocumentPart().getNumberingDefinitionsPart();
        
        if (sourceNumbering != null && targetNumbering == null) {
            targetDoc.getMainDocumentPart().addTargetPart(sourceNumbering);
        }
    }
    
    private void addPageBreak(MainDocumentPart mainDocumentPart) {
        P pageBreakParagraph = Context.getWmlObjectFactory().createP();
        R run = Context.getWmlObjectFactory().createR();
        Br pageBreak = Context.getWmlObjectFactory().createBr();
        pageBreak.setType(STBrType.PAGE);
        run.getContent().add(pageBreak);
        pageBreakParagraph.getContent().add(run);
        mainDocumentPart.addObject(pageBreakParagraph);
    }
    
    private P createTOCTitle(String title) {
        P tocTitleParagraph = Context.getWmlObjectFactory().createP();
        R run = Context.getWmlObjectFactory().createR();
        Text text = Context.getWmlObjectFactory().createText();
        text.setValue(title);
        run.getContent().add(text);
        
        // Apply heading style
        PPr paragraphProperties = Context.getWmlObjectFactory().createPPr();
        PStyle pStyle = Context.getWmlObjectFactory().createPPrBasePStyle();
        pStyle.setVal("Heading1");
        paragraphProperties.setPStyle(pStyle);
        tocTitleParagraph.setPPr(paragraphProperties);
        
        tocTitleParagraph.getContent().add(run);
        return tocTitleParagraph;
    }
    
    private P createTOCField() {
        P tocParagraph = Context.getWmlObjectFactory().createP();
        R run = Context.getWmlObjectFactory().createR();
        
        // Create TOC field
        FldChar fldCharBegin = Context.getWmlObjectFactory().createFldChar();
        fldCharBegin.setFldCharType(STFldCharType.BEGIN);
        run.getContent().add(fldCharBegin);
        
        R runInstr = Context.getWmlObjectFactory().createR();
        Text instrText = Context.getWmlObjectFactory().createText();
        instrText.setValue(" TOC \\o \"1-3\" \\h \\z \\u ");
        runInstr.getContent().add(instrText);
        
        R runEnd = Context.getWmlObjectFactory().createR();
        FldChar fldCharEnd = Context.getWmlObjectFactory().createFldChar();
        fldCharEnd.setFldCharType(STFldCharType.END);
        runEnd.getContent().add(fldCharEnd);
        
        tocParagraph.getContent().add(run);
        tocParagraph.getContent().add(runInstr);
        tocParagraph.getContent().add(runEnd);
        
        return tocParagraph;
    }
    
    private void addSectionTitle(WordprocessingMLPackage doc, String sectionTitle) {
        MainDocumentPart mainDoc = doc.getMainDocumentPart();
        
        P titleParagraph = Context.getWmlObjectFactory().createP();
        R run = Context.getWmlObjectFactory().createR();
        Text text = Context.getWmlObjectFactory().createText();
        text.setValue(sectionTitle);
        run.getContent().add(text);
        
        // Apply heading style
        PPr paragraphProperties = Context.getWmlObjectFactory().createPPr();
        PStyle pStyle = Context.getWmlObjectFactory().createPPrBasePStyle();
        pStyle.setVal("Heading2");
        paragraphProperties.setPStyle(pStyle);
        titleParagraph.setPPr(paragraphProperties);
        
        titleParagraph.getContent().add(run);
        mainDoc.addObject(titleParagraph);
    }
    
    private void mergeAtBeginning(WordprocessingMLPackage baseDoc, WordprocessingMLPackage docToMerge, 
                                 MergeParameters params) throws Exception {
        MainDocumentPart baseMainDoc = baseDoc.getMainDocumentPart();
        MainDocumentPart sourceMainDoc = docToMerge.getMainDocumentPart();
        
        List<Object> baseContent = new ArrayList<>(baseMainDoc.getContent());
        baseMainDoc.getContent().clear();
        
        // Add source content first
        for (Object contentElement : sourceMainDoc.getContent()) {
            baseMainDoc.addObject(contentElement);
        }
        
        if (params.isAddPageBreak()) {
            addPageBreak(baseMainDoc);
        }
        
        // Add original base content
        for (Object contentElement : baseContent) {
            baseMainDoc.addObject(contentElement);
        }
    }
    
    private void mergeAtEnd(WordprocessingMLPackage baseDoc, WordprocessingMLPackage docToMerge, 
                           MergeParameters params) throws Exception {
        mergeDocumentContent(baseDoc, docToMerge, params.isAddPageBreak(), params.isPreserveStyles());
    }
    
    private void mergeAfterTOC(WordprocessingMLPackage baseDoc, WordprocessingMLPackage docToMerge, 
                              MergeParameters params) throws Exception {
        // Implementation to merge after table of contents
        // This would require finding TOC elements and inserting after them
        mergeAtEnd(baseDoc, docToMerge, params);
    }
}