package com.bungo.bungoaiagent.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author bungosama_
 * @since 2025-07-03 18:22
 */
class PDFGenerationToolTest {

    @Test
    void generatePDF() {
        PDFGenerationTool pdfGenerationTool = new PDFGenerationTool();
        String result = pdfGenerationTool.generatePDF("test.pdf", "This is a test PDF.");
        Assertions.assertNotNull(result);
    }
}