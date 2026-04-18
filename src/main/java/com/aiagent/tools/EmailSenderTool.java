package com.aiagent.tools;

public class EmailSenderTool implements Tool {

    @Override
    public String getName() {
        return "email_sender";
    }

    @Override
    public String getDescription() {
        return "Simulates sending an email. Input format: 'to@example.com | Subject | Body'";
    }

    @Override
    public String execute(String input) {
        // NOTE: This is a simulated tool — does not actually send email.
        // It demonstrates the Tool interface and Strategy Pattern.
        try {
            String[] parts = input.split("\\|");
            if (parts.length < 3) {
                return "Error: Input must be in format 'to@example.com | Subject | Body'";
            }
            String to = parts[0].trim();
            String subject = parts[1].trim();
            String body = parts[2].trim();

            return "[SIMULATED] Email sent to: " + to + " | Subject: " + subject + " | Body: " + body;
        } catch (Exception e) {
            return "Error: Input must be in format 'to@example.com | Subject | Body'";
        }
    }
}