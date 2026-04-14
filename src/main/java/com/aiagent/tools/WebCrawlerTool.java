package com.aiagent.tools;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class WebCrawlerTool implements Tool{
    @Override
    public String getName(){
        return "web_crawler";
    }

    @Override
    public String getDescription(){
        return "Fetches the 500 texts of a webpage. Input format: 'http://example.com'";
    }

    @Override
    public String execute(String input){
        try{
            Document doc=Jsoup.connect(input.trim()).get();
            String text=doc.body().text();
            return text.substring(0,Math.min(text.length(),500));
        }
        catch(Exception e){
            return "Error fetching site: "+e.getMessage();
        }
    }

}