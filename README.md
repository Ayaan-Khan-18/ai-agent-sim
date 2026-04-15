# 🤖 AI Agent Workflow Manager

A Java-based autonomous AI agent that uses a local LLM (via Ollama) as its "brain" and a set of pluggable tools as its "body". The agent receives user input, thinks using the LLM, and decides which tool to use to complete the task.

Built as a Java OOP course project demonstrating core design patterns in a real, working system.

---

## 🧠 How It Works

```
User Input
    ↓
Agent receives message
    ↓
LLMClient sends message to Ollama (local AI)
    ↓
LLM decides: "I need to use a tool"
    ↓
ToolCallParser extracts tool name + input
    ↓
ToolRegistry finds the right Tool
    ↓
Tool.execute() runs and returns result
    ↓
Result sent back to LLM → Final response to user
```

---

## 🏗️ Project Structure

```
AIAgentProject/
├── pom.xml
└── src/
    └── main/
        └── java/
            └── com/
                └── aiagent/
                    ├── Main.java                  ← Entry point, CLI loop
                    ├── agent/
                    │   ├── Agent.java             ← Core agent (Composition hub)
                    │   ├── Memory.java            ← Conversation history
                    │   └── AgentState.java        ← Enum: IDLE, THINKING, ACTING
                    ├── llm/
                    │   ├── LLMClient.java         ← Interface (abstraction)
                    │   └── OllamaClient.java      ← Hits localhost:11434
                    ├── tools/
                    │   ├── Tool.java              ← Strategy Pattern interface
                    │   ├── ToolRegistry.java      ← Singleton
                    │   ├── CalculatorTool.java
                    │   ├── FileReaderTool.java
                    │   ├── WebCrawlerTool.java
                    │   └── EmailSenderTool.java
                    ├── observer/
                    │   ├── AgentObserver.java     ← Observer Pattern interface
                    │   ├── EventBus.java          ← Notifies all observers
                    │   └── LoggerObserver.java    ← Logs state changes
                    └── parser/
                        └── ToolCallParser.java    ← Parses LLM output for tool calls
```

---


## 🚀 Getting Started

### Prerequisites
- Java 17+
- Maven
- [Ollama](https://ollama.com) installed and running locally

### Pull a model in Ollama
```bash
ollama pull gemma3:9b
# or
ollama pull qwen2.5-coder:7b
```

### Build the project
```bash
mvn compile
```

### Run the agent
```bash
mvn exec:java -Dexec.mainClass="com.aiagent.Main"
```

Or build a JAR and run it:
```bash
mvn package
java -jar target/ai-agent.jar
```

---


## 📚 Dependencies

| Library | Version | Purpose |
|---------|---------|---------|
| Gson | 2.10.1 | Parse JSON responses from Ollama API |
| Jsoup | 1.17.2 | Fetch and parse HTML from web pages |
