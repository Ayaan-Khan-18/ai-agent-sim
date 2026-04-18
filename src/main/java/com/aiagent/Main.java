package com.aiagent;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.aiagent.agent.Agent;
import com.aiagent.llm.LLMClient;
// import com.aiagent.llm.LMStudioClient;
import com.aiagent.llm.GroqClient;
import com.aiagent.observer.EventBus;
import com.aiagent.observer.LoggerObserver;
import com.aiagent.tools.*;
import java.util.Scanner;
import io.github.cdimascio.dotenv.Dotenv;



public class Main {

    // private static String selectModel() {
    //     System.out.println("Fetching available models from LM Studio...");
    //     try {
    //         // fetch available models from LM Studio
    //         HttpClient client = HttpClient.newHttpClient();
    //         HttpRequest request = HttpRequest.newBuilder()
    //             .uri(URI.create("http://localhost:1234/v1/models"))
    //             .GET()
    //             .build();

    //         HttpResponse<String> response = client.send(
    //             request, HttpResponse.BodyHandlers.ofString()
    //         );

    //         // parse model list
    //         JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
    //         JsonArray models = json.getAsJsonArray("data");

    //         System.out.println("\n========================================");
    //         System.out.println("   Available Models in LM Studio");
    //         System.out.println("========================================");

    //         for (int i = 0; i < models.size(); i++) {
    //             String modelId = models.get(i).getAsJsonObject()
    //                                 .get("id").getAsString();
    //             System.out.println("[" + i + "] " + modelId);
    //         }

    //         // user picks
    //         System.out.print("\nSelect model number: ");
    //         Scanner scanner = new Scanner(System.in);
    //         int choice = Integer.parseInt(scanner.nextLine().trim());

    //         return models.get(choice).getAsJsonObject()
    //                     .get("id").getAsString();

    //     } catch (Exception e) {
    //         System.out.println("Could not fetch models: " + e.getMessage());
    //         System.out.print("Enter model name manually: ");
    //         return new Scanner(System.in).nextLine().trim();
    //     }
    // }

    public static void main(String[] args) {

        Dotenv dotenv = Dotenv.load();
        String apiKey = dotenv.get("GROQ_API_KEY");
        String modelName = dotenv.get("GROQ_MODEL");

        //observer
    EventBus eventBus = new EventBus();
    eventBus.subscribe(new LoggerObserver());
    //tools
    ToolRegistry registry = ToolRegistry.getInstance();
        registry.register(new CalculatorTool());
        registry.register(new FileReaderTool());
        registry.register(new WebCrawlerTool());
        registry.register(new EmailSenderTool());
    //LLM Client
    // String modelName = "qwen/qwen3-8b";//selectModel();
    // LMStudioClient llmClient = new LMStudioClient(modelName);
    LLMClient llmClient = new GroqClient(apiKey, modelName);
    //Agent
    Agent agent = new Agent(llmClient, registry, eventBus);
    //CLI
    Scanner scanner = new Scanner(System.in);
        System.out.println("========================================");
        System.out.println("   AI Agent Workflow Manager");
        System.out.println("   Powered by LM Studio");
        System.out.println("========================================");
        System.out.println("Type 'exit' to quit\n");

    while (true) {
            System.out.print("You: ");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Goodbye!");
                break;
            }

            String response = agent.chat(input);
            System.out.println("Agent: " + response + "\n");
        }

        scanner.close();
    }
}