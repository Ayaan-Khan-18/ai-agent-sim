package com.aiagent.tools;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class CalculatorTool implements Tool {

    @Override
    public String getName() {
        return "calculator";
    }

    @Override
    public String getDescription() {
        return "Evaluates math expressions. Input examples: '10 + 5', '10 * 10 * 10', '100 / 4'";
    }

    @Override
    public String execute(String input) {
        try {
            // remove any spaces and validate only safe characters
            String expr = input.trim();
            if (!expr.matches("[0-9+\\-*/.() ]+")) {
                return "Error: Invalid expression '" + expr + "'. Only numbers and + - * / ( ) allowed.";
            }

            ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
            Object result = engine.eval(expr);

            double val = Double.parseDouble(result.toString());
            if (val == Math.floor(val)) {
                return String.valueOf((long) val);
            }
            return String.valueOf(val);

        } catch (Exception e) {
            return "Error evaluating expression: " + e.getMessage();
        }
    }
}