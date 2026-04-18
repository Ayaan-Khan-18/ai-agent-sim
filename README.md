# 🤖 AI Agent Workflow Manager

A Java-based autonomous AI agent that uses a local LLM (via Ollama) as its "brain" and a set of pluggable tools as its "body". The agent receives user input, thinks using the LLM, and decides which tool to use to complete the task.

Built as a Java OOP course project demonstrating core design patterns in a real, working system.

---

## 🧠 How It Works

```
User types message
        ↓
   Agent.java (orchestrator)
        ↓                    ↓
  GroqClient            EventBus
  (AI brain)            (logs events)
        ↓
  LLM Response
        ↓
  Contains "TOOL:"?
    ↓           ↓
   YES           NO
    ↓           ↓
ToolRegistry   return
finds tool     response
    ↓
Tool.execute()
    ↓
return result
```

---

## 🏗️ Project Structure

```
AIAgentProject/
├── pom.xml
├── .env                           ← API keys (never commit this!)
├── .gitignore
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── aiagent/
        │           ├── Main.java              ← CLI entry point
        │           ├── AgentServer.java       ← Web UI entry point
        │           ├── agent/
        │           │   ├── Agent.java         ← Core agent (Composition hub)
        │           │   └── Memory.java        ← Conversation history
        │           ├── llm/
        │           │   ├── LLMClient.java     ← Interface (abstraction)
        │           │   ├── GroqClient.java    ← Groq cloud API
        │           │   └── LMStudioClient.java← Local LLM (LM Studio)
        │           ├── tools/
        │           │   ├── Tool.java          ← Strategy Pattern interface
        │           │   ├── ToolRegistry.java  ← Singleton
        │           │   ├── CalculatorTool.java
        │           │   ├── FileReaderTool.java
        │           │   ├── WebCrawlerTool.java
        │           │   └── EmailSenderTool.java
        │           └── observer/
        │               ├── AgentObserver.java ← Observer Pattern interface
        │               ├── EventBus.java      ← Notifies all observers
        │               └── LoggerObserver.java← Logs state changes
        └── resources/
            └── public/
                └── index.html               ← Web dashboard UI
```

---


## 🚀 Getting Started

### Prerequisites
- Java 17+
- Maven
- Groq API key (free at console.groq.com) OR LM Studio running locally

### 1. Set up `.env`

Create a `.env` file in the project root:

```env
GROQ_API_KEY=your-groq-api-key-here
GROQ_MODEL=llama-3.1-8b-instant
```

### 2. Build

```bash
mvn clean package -DskipTests
```

### 3. Run — CLI mode

```bash
java -jar target/ai-agent.jar
```

### 4. Run — Web UI mode

```bash
java -cp target/ai-agent.jar com.aiagent.AgentServer
```

Then open **http://localhost:8080** in your browser.

```

## 📦 Dependencies

| Library | Version | Purpose |
|---------|---------|---------|
| Gson | 2.10.1 | Parse JSON from LLM APIs |
| Jsoup | 1.17.2 | Fetch and parse HTML from web pages |
| dotenv-java | 3.0.0 | Load `.env` file for API keys |
| Javalin | 6.1.3 | Lightweight web server for dashboard |
| slf4j-simple | 2.0.9 | Logging for Javalin |

---

## 🔐 Environment Variables

| Variable | Description |
|----------|-------------|
| `GROQ_API_KEY` | Your Groq API key from console.groq.com |
| `GROQ_MODEL` | Model name e.g. `llama-3.1-8b-instant` |