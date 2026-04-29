package com.aiagent.tools;

/**
 * Evaluates math expressions using a recursive descent parser.
 * Supports: +, -, *, /, parentheses, and decimal numbers.
 * No external dependencies — works on Java 17+.
 */
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
            String expr = input.trim();
            if (!expr.matches("[0-9+\\-*/.() ]+")) {
                return "Error: Invalid expression '" + expr + "'. Only numbers and + - * / ( ) allowed.";
            }

            double result = evaluate(expr);

            if (result == Math.floor(result) && !Double.isInfinite(result)) {
                return String.valueOf((long) result);
            }
            return String.valueOf(result);

        } catch (Exception e) {
            return "Error evaluating expression: " + e.getMessage();
        }
    }

    // ── Recursive Descent Parser ──

    private int pos;
    private String expression;

    private double evaluate(String expr) {
        this.expression = expr.replaceAll("\\s+", "");
        this.pos = 0;
        double result = parseExpression();
        if (pos < expression.length()) {
            throw new RuntimeException("Unexpected character: " + expression.charAt(pos));
        }
        return result;
    }

    // Handles + and -
    private double parseExpression() {
        double result = parseTerm();
        while (pos < expression.length()) {
            char op = expression.charAt(pos);
            if (op == '+') {
                pos++;
                result += parseTerm();
            } else if (op == '-') {
                pos++;
                result -= parseTerm();
            } else {
                break;
            }
        }
        return result;
    }

    // Handles * and /
    private double parseTerm() {
        double result = parseFactor();
        while (pos < expression.length()) {
            char op = expression.charAt(pos);
            if (op == '*') {
                pos++;
                result *= parseFactor();
            } else if (op == '/') {
                pos++;
                double divisor = parseFactor();
                if (divisor == 0) {
                    throw new ArithmeticException("Division by zero");
                }
                result /= divisor;
            } else {
                break;
            }
        }
        return result;
    }

    // Handles numbers and parentheses
    private double parseFactor() {
        // Handle negative sign
        if (pos < expression.length() && expression.charAt(pos) == '-') {
            pos++;
            return -parseFactor();
        }

        // Handle parentheses
        if (pos < expression.length() && expression.charAt(pos) == '(') {
            pos++; // skip '('
            double result = parseExpression();
            if (pos < expression.length() && expression.charAt(pos) == ')') {
                pos++; // skip ')'
            } else {
                throw new RuntimeException("Missing closing parenthesis");
            }
            return result;
        }

        // Parse number
        int start = pos;
        while (pos < expression.length() &&
               (Character.isDigit(expression.charAt(pos)) || expression.charAt(pos) == '.')) {
            pos++;
        }
        if (start == pos) {
            throw new RuntimeException("Expected a number at position " + pos);
        }
        return Double.parseDouble(expression.substring(start, pos));
    }
}