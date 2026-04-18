package com.aiagent.llm;

public interface LLMClient {
    String chat(String systemPrompt, String userMessage);
}