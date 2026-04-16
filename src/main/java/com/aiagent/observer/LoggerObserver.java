package com.aiagent.observer;

public class LoggerObserver implements AgentObserver {
    @Override
    public void onEvent(String eventMessage){
        System.out.println("[AGENT EVENT] " + eventMessage);
    }
}
