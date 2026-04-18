package com.aiagent;

import com.aiagent.agent.Agent;
import com.aiagent.llm.GroqClient;
import com.aiagent.observer.EventBus;
import com.aiagent.observer.AgentObserver;
import com.aiagent.tools.*;
import io.github.cdimascio.dotenv.Dotenv;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import java.util.ArrayList;
import java.util.List;

public class AgentServer {

    // stores events for the current request
    static List<String> currentEvents = new ArrayList<>();

    public static void main(String[] args) {
        // setup
        Dotenv dotenv = Dotenv.load();
        
        // event bus that captures events into our list
        EventBus eventBus = new EventBus();
        eventBus.subscribe(new AgentObserver() {
            @Override
            public void onEvent(String event) {
                currentEvents.add(event);
                System.out.println("[EVENT] " + event);
            }
        });

        // tools
        ToolRegistry registry = ToolRegistry.getInstance();
        registry.register(new CalculatorTool());
        registry.register(new FileReaderTool());
        registry.register(new WebCrawlerTool());
        registry.register(new EmailSenderTool());

        // agent
        GroqClient llm = new GroqClient(
            dotenv.get("GROQ_API_KEY"),
            dotenv.get("GROQ_MODEL")
        );
        Agent agent = new Agent(llm, registry, eventBus);

        // web server
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public", Location.CLASSPATH);
        }).start(8080);

        // API endpoint — receives message, returns response + events
        app.post("/chat", ctx -> {
            currentEvents.clear();

            String userMessage = ctx.body();

            String response = agent.chat(userMessage);

            // build JSON response
            JsonObject result = new JsonObject();
            result.addProperty("response", response);

            JsonArray events = new JsonArray();
            for (String event : currentEvents) {
                events.add(event);
            }
            result.add("events", events);

            ctx.json(result.toString());
        });

        System.out.println("Agent UI running at http://localhost:8080");
    }
}