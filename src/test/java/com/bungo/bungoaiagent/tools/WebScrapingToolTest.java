package com.bungo.bungoaiagent.tools;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author bungosama_
 * @since 2025-07-03 17:24
 */
class WebScrapingToolTest {

    @Test
    void scrapeWebPage() {
        WebScrapingTool webScrapingTool = new WebScrapingTool();
        String result = webScrapingTool.scrapeWebPage("https://www.codefather.cn");
        Assertions.assertNotNull(result);
    }
}