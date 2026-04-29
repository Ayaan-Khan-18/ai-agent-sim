package com.aiagent.tools;

import io.github.cdimascio.dotenv.Dotenv;
import java.util.Properties;

/**
 * Encapsulates all SMTP configuration for email sending.
 * 
 * OOP Concepts:
 * - Encapsulation: SMTP credentials and settings are private, accessed only through getters
 * - Single Responsibility: This class only handles email configuration, nothing else
 */
public class EmailConfig {
    private final String smtpHost;
    private final int smtpPort;
    private final String username;
    private final String password;
    private final boolean useTLS;

    /**
     * Loads email configuration from environment variables.
     * Uses dotenv-java to read from the .env file.
     */
    public EmailConfig() {
        Dotenv dotenv = Dotenv.load();
        this.username = dotenv.get("EMAIL_USER");
        this.password = dotenv.get("EMAIL_APP_PASSWORD");
        this.smtpHost = "smtp.gmail.com";
        this.smtpPort = 587;
        this.useTLS = true;

        if (username == null || username.isEmpty()) {
            throw new IllegalStateException("EMAIL_USER is not set in .env file");
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalStateException("EMAIL_APP_PASSWORD is not set in .env file");
        }
    }

    /**
     * Builds the Java Mail Properties object with all SMTP settings.
     * Encapsulates the complexity of SMTP configuration behind a clean API.
     */
    public Properties toMailProperties() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", String.valueOf(useTLS));
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", String.valueOf(smtpPort));
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        return props;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getSmtpHost() {
        return smtpHost;
    }

    public int getSmtpPort() {
        return smtpPort;
    }
}
