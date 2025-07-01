package com.bungo.bungoaiagent.rag;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author bungosama_
 * @since 2025-06-28 04:33
 */
@SpringBootTest
class LoveAppDocumentLoaderTest {

    @Resource
    private LoveAppDocumentLoader loveAppDocumentLoader;

    @Test
    void loadDocument() {
        loveAppDocumentLoader.loadMarkdownDocuments();
    }

}