package com.aiagent.agent;

import java.util.ArrayList;
import java.util.List;

public class Memory {
    private List<String> history=new ArrayList<String>();

    public void addMessage(String role, String message){
        history.add(role + ": " + message);
    }

    public List<String> getHistory() {
        return history;
    }

    public String getFormattedHistory(){
        return String.join("\n", history);

    }
}
