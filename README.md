File structure:

AIAgentProject/
├── src/
│   └── main/java/com/aiagent/ 
│       ├── Main.java                    ← Entry point, CLI loop
│       ├── agent/
│       │   ├── Agent.java               ← Core agent (Composition hub)
│       │   ├── Memory.java              ← Stores conversation history
│       │   └── AgentState.java          ← Enum: IDLE, THINKING, ACTING
│       ├── llm/
│       │   ├── LLMClient.java           ← Interface (abstraction)
│       │   └── OllamaClient.java        ← Hits localhost:11434
│       ├── tools/
│       │   ├── Tool.java                ← Interface (Strategy Pattern)
│       │   ├── ToolRegistry.java        ← Holds all tools
│       │   ├── CalculatorTool.java
│       │   ├── FileReaderTool.java
│       │   ├── WebCrawlerTool.java
│       │   └── EmailSenderTool.java
│       ├── observer/
│       │   ├── AgentObserver.java       ← Interface (Observer Pattern)
│       │   ├── EventBus.java            ← Notifies observers
│       │   └── LoggerObserver.java      ← Prints state changes
│       └── parser/
│           └── ToolCallParser.java      ← Parses LLM output for tool calls
└── pom.xml

Plan:
Layer 1 — Tools (Strategy Pattern)
  → Tool.java interface
  → CalculatorTool.java
  → FileReaderTool.java
  → WebCrawlerTool.java
  → ToolRegistry.java (Singleton)

Layer 2 — Observer (Event System)
  → AgentObserver.java interface
  → EventBus.java
  → LoggerObserver.java

Layer 3 — LLM Client (Abstraction)
  → LLMClient.java interface
  → OllamaClient.java

Layer 4 — Agent Core (Composition)
  → Memory.java
  → Agent.java

Layer 5 — Glue
  → ToolCallParser.java
  → Main.java
