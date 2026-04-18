package com.aiagent.agent;

import com.aiagent.llm.LLMClient;
import com.aiagent.observer.EventBus;
import com.aiagent.tools.ToolRegistry;

public class Agent {
    private Memory memory;
    private ToolRegistry toolRegistry;
    private LLMClient llmClient;
    private EventBus eventBus;

    public Agent(LLMClient llmClient, ToolRegistry toolRegistry, EventBus eventBus) {
        this.llmClient = llmClient;
        this.toolRegistry = toolRegistry;
        this.eventBus = eventBus;
        this.memory = new Memory();
    }

    private String buildSystemPrompt() {
        StringBuilder sb = new StringBuilder();
        sb.append("You are an AI assistant with tools.\n");
        sb.append("To use a tool respond EXACTLY:\nTOOL: name\nINPUT: value\n\n");
        sb.append("Tools:\n");
        for (var tool : toolRegistry.getAllTools()) {
            sb.append("- " + tool.getName() + ": " + tool.getDescription() + "\n");
        }
        return sb.toString();
    }
    public String chat(String userMessage){
        memory.addMessage("user", userMessage);
        eventBus.publish("User said: "+userMessage);

        eventBus.publish("State changed: THINKING");
        String llmResponse = llmClient.chat(buildSystemPrompt(), userMessage);

        if(llmResponse.contains("TOOL:")){
            eventBus.publish("StateChnaged: ACTING");
            String toolResult=handleToolCall(llmResponse);
            memory.addMessage("assistant", toolResult);
            eventBus.publish("Tool called, result: "+toolResult);
            return toolResult;
        }

        eventBus.publish("State changed: IDLE");
        memory.addMessage("assistant", llmResponse);
        return llmResponse;
    }

    private String handleToolCall(String llmResponse){
        String[] lines=llmResponse.split("\n");
        String toolName="";
        String toolInput="";
        for(String line:lines){
            if(line.startsWith("TOOL:")){
                toolName=line.replace("TOOL:", "").trim();
            } else if(line.startsWith("INPUT:")){
                toolInput=line.replace("INPUT:", "").trim();
            }
        }
        var tool=toolRegistry.getTool(toolName);
        if(tool==null){
            return "Unknown tool "+toolName+" not found.";
        }
        eventBus.publish("Using tool: " + toolName + " with input: " + toolInput);
        return tool.execute(toolInput);
    }

}
