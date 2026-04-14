package com.aiagent.tools;

public interface Tool {
    String getName();
    String getDescription();
    String execute(String input);
}
