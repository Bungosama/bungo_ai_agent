package com.bungo.bungoaiagent.tools;

import org.jsoup.Jsoup;

import org.jsoup.nodes.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.IOException;

/**
 * @author bungosama_
 * @since 2025-07-03 17:20
 * 网页抓取工具
 */
public class WebScrapingTool {

    @Tool(description = "scrape the content of a web page")
    public String scrapeWebPage(@ToolParam(description = "URL of the web page to scrape") String url) {
        try {
            Document document = Jsoup.connect(url).get();
            return document.html();
        } catch (Exception e) {
            return "Error scraping web page: " + e.getMessage();
        }
    }

}
