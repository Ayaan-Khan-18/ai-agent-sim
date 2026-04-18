package com.aiagent.llm;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class LMStudioClient implements LLMClient {
    
    private String modelName;

    public LMStudioClient(String modelName){
        this.modelName = modelName;
    }

    @Override
    public String chat(String systemPrompt, String userMessage) {
        try{
            //build jason
            JsonObject body=new JsonObject();
            body.addProperty("model", modelName);

            JsonArray messages=new JsonArray();
            JsonObject systemMsg=new JsonObject();
            systemMsg.addProperty("role", "system");
            systemMsg.addProperty("content", systemPrompt);
            messages.add(systemMsg);

            JsonObject userMsg=new JsonObject();
            userMsg.addProperty("role", "user");
            userMsg.addProperty("content", userMessage);
            messages.add(userMsg);

            body.add("messages", messages);
            body.addProperty("temperature", 0.7);
            body.addProperty("max_tokens", 200);

            JsonObject extra = new JsonObject();
            extra.addProperty("thinking", false);
            body.add("chat_template_kwargs", extra);
            
            //send POST request
            HttpClient client=HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:1234/v1/chat/completions"))
            .header("Content-Type", "application/json")
            .timeout(java.time.Duration.ofSeconds(180))  // ← add this
            .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
            .build();
                    System.out.println("DEBUG: Sending request to LM Studio...");
            System.out.println("DEBUG: Body = " + body.toString());
            //get response
            HttpResponse<String> response=client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("DEBUG: Got response!"); 

            //extract text
            JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
            return json.getAsJsonArray("choices")
                       .get(0).getAsJsonObject()
                       .getAsJsonObject("message")
                       .get("content").getAsString();
        } catch (Exception e){
            e.printStackTrace();
            return "LMStudio Error: " + e.getMessage();
        }
    }
}
