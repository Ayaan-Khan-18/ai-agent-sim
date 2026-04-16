package com.aiagent.tools;

import java.util.HashMap;
import java.util.Collection;

public class ToolRegistry {
    private static ToolRegistry instance;

    private HashMap<String, Tool> tools=new HashMap<>();

    private ToolRegistry(){}

    public static ToolRegistry getInstance(){
        if(instance==null){
            instance=new ToolRegistry();
        }
        return instance;
    }
    public void register(Tool tool){
        tools.put(tool.getName(), tool);
    }

    public Tool getTool(String name){
        return tools.get(name);
    }

    public Collection<Tool> getAllTools(){
        return tools.values();
    }
}
