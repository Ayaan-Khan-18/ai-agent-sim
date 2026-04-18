package com.aiagent.agent;

import com.aiagent.llm.LLMClient;
import com.aiagent.observer.EventBus;
import com.aiagent.tools.ToolRegistry;

public class Agent {
    private Memory memory;
    private ToolRegistry toolRegistry;
    private LLMClient llmClient;
    private EventBus eventBus;
    private AgentState state = AgentState.IDLE;

    public Agent(LLMClient llmClient, ToolRegistry toolRegistry, EventBus eventBus) {
        this.llmClient = llmClient;
        this.toolRegistry = toolRegistry;
        this.eventBus = eventBus;
        this.memory = new Memory();
    }

    private String buildSystemPrompt() {
        StringBuilder sb = new StringBuilder();
        sb.append("You are an autonomous AI Agent.\n");
        sb.append("RULES:\n");
        sb.append("1. If you need a tool, you must reply with EXACTLY this format and nothing else:\n");
        sb.append("TOOL: [tool_name]\n");
        sb.append("INPUT: [tool_input]\n");
        sb.append("2. DO NOT write any conversational text before or after the tool call.\n");
        sb.append("3. DO NOT simulate or guess the tool's output.\n");
        sb.append("4. STOP generating text immediately after requesting a tool.\n\n");
        sb.append("Tools available:\n");
        for (var tool : toolRegistry.getAllTools()) {
            sb.append("- ").append(tool.getName()).append(": ").append(tool.getDescription()).append("\n");
        }
        return sb.toString();
    }

    public String chat(String userMessage) {
        memory.addMessage("user", userMessage);
        eventBus.publish("User said: " + userMessage);

        state = AgentState.THINKING;
        eventBus.publish("State changed: " + state);
        String llmResponse = llmClient.chat(buildSystemPrompt(), userMessage);

        if (llmResponse.contains("TOOL:")) {
            state = AgentState.ACTING;
            eventBus.publish("State changed: " + state);
            String toolResult = handleToolCall(llmResponse);
            memory.addMessage("assistant", toolResult);
            eventBus.publish("Tool result: " + toolResult);

            state = AgentState.IDLE;
            eventBus.publish("State changed: " + state);
            return toolResult;
        }

        state = AgentState.IDLE;
        eventBus.publish("State changed: " + state);
        memory.addMessage("assistant", llmResponse);
        return llmResponse;
    }

    private String handleToolCall(String llmResponse) {
        String[] lines = llmResponse.split("\n");
        String toolName = "";
        String toolInput = "";
        for (String line : lines) {
            if (line.startsWith("TOOL:")) {
                toolName = line.replace("TOOL:", "").trim();
            } else if (line.startsWith("INPUT:")) {
                toolInput = line.replace("INPUT:", "").trim();
            }
        }

        var tool = toolRegistry.getTool(toolName);
        if (tool == null) {
            return "System Error: Unknown tool '" + toolName + "' not found.";
        }

        eventBus.publish("Using tool: " + toolName + " with input: " + toolInput);

        // The Senior Dev Fault Isolation Upgrade!
        try {
            return tool.execute(toolInput);
        } catch (Exception e) {
            return "System Error: Tool crashed unexpectedly: " + e.getMessage();
        }
    }

    public AgentState getState() {
        return state;
    }
}