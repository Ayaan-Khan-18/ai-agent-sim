package com.aiagent.tools;

/**
 * Encapsulates a parsed email message with recipient, subject, and body.
 * 
 * OOP Concepts:
 * - Encapsulation: Fields are private and immutable (set only via constructor)
 * - Single Responsibility: Only responsible for parsing and holding email data
 * - Data Validation: Constructor validates input format on creation
 */
public class EmailMessage {
    private final String to;
    private final String subject;
    private final String body;

    /**
     * Parses a pipe-delimited input string into an EmailMessage.
     * Expected format: "to@example.com | Subject | Body"
     *
     * @param rawInput the raw pipe-delimited string from the LLM
     * @throws IllegalArgumentException if the format is invalid
     */
    public EmailMessage(String rawInput) {
        if (rawInput == null || rawInput.isEmpty()) {
            throw new IllegalArgumentException("Email input cannot be empty");
        }

        String[] parts = rawInput.split("\\|");
        if (parts.length < 3) {
            throw new IllegalArgumentException(
                "Invalid format. Expected: 'to@example.com | Subject | Body'"
            );
        }

        this.to = parts[0].trim();
        this.subject = parts[1].trim();
        // Join remaining parts in case body contains pipe characters
        StringBuilder bodyBuilder = new StringBuilder(parts[2].trim());
        for (int i = 3; i < parts.length; i++) {
            bodyBuilder.append("|").append(parts[i]);
        }
        this.body = bodyBuilder.toString();

        // Basic email validation
        if (!to.contains("@") || !to.contains(".")) {
            throw new IllegalArgumentException(
                "Invalid email address: '" + to + "'"
            );
        }
    }

    public String getTo() {
        return to;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "EmailMessage{to='" + to + "', subject='" + subject + "'}";
    }
}
