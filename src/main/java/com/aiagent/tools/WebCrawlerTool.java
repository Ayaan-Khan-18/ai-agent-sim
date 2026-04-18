package com.aiagent.tools;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class WebCrawlerTool implements Tool {

    @Override
    public String execute(String input) {
        if (input == null || !input.startsWith("http")) {
            return "System Error: Invalid URL. URL must start with http or https.";
        }

        try {
            Document doc = Jsoup.connect(input.trim()).userAgent("Mozilla/5.0").get();

            Elements paragraphs = doc.select("p");
            String cleanText = paragraphs.text();

            if (cleanText.isEmpty()) {
                return "System Error: Page loaded, but no readable paragraphs were found.";
            }

            // SMART TRUNCATION: End at the last full stop before the limit
            if (cleanText.length() > 1000) {
                // Grab the first 1000 characters
                String chunk = cleanText.substring(0, 1000);

                // Find where the last '.' is located inside that chunk
                int lastFullStop = chunk.lastIndexOf('.');

                if (lastFullStop != -1) {
                    // Cut it right after the last full stop to complete the sentence
                    return chunk.substring(0, lastFullStop + 1) + " [CONTENT TRUNCATED]";
                } else {
                    // Fallback just in case there are no periods at all
                    return chunk + "... [CONTENT TRUNCATED]";
                }
            }

            return cleanText;

        } catch (Exception e) {
            return "System Error: Web Crawler failed to fetch the URL - " + e.getMessage();
        }
    }
}