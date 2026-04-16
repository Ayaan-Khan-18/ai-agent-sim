package com.aiagent.observer;

import java.util.ArrayList;
import java.util.List;

public class EventBus {
    private List<AgentObserver> observers = new ArrayList<>(); 
    public void subscribe(AgentObserver observer) {
        // Add observer to the list
        observers.add(observer);
    }
    public void publish(String event) {
        // Notify all observers with the event message
        for (AgentObserver observer : observers) {
            observer.onEvent(event);
        }   
    }
}
