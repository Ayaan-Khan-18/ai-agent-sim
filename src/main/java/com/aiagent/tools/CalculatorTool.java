package com.aiagent.tools;

public class CalculatorTool implements Tool {
    @Override
    public String getName() {
        return "calculator";
    }

    @Override
    public String getDescription() {
        return "Evaluates math expressions. Input format: '10 + 5'";
    }
    
    @Override
    public String execute(String input) {
        String[] parts=input.trim().split(" ");
        double num1=Double.parseDouble(parts[0]);
        double num2=Double.parseDouble(parts[2]);
        String op=parts[1];
        double result=0;
        switch (op) {
            case "+":
                result=num1+num2;
                break;
            case "-":
                result=num1-num2;
                break;
            case "*":
                result=num1*num2;
                break;
            case "/":
                if (num2 == 0) return "Error: division by zero";
                result = num1 / num2;
                break;
            default:
                return "Unsupported operator: "+op;    
        }
        return String.valueOf(result);
    }
}
