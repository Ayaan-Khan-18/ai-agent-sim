package com.aiagent.tools;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

/**
 * Sends real emails via Gmail SMTP.
 * 
 * OOP Concepts:
 * - Strategy Pattern: Implements the Tool interface — Agent calls execute() without knowing the internals
 * - Composition: Uses EmailConfig (SMTP settings) and EmailMessage (parsed data) as composed objects
 * - Encapsulation: SMTP connection details are hidden inside EmailConfig, parsing inside EmailMessage
 * - Single Responsibility: This class only orchestrates the send — config and parsing are delegated
 */
public class EmailSenderTool implements Tool {

    private final EmailConfig config;

    /**
     * Constructor loads SMTP configuration eagerly.
     * Composition: EmailSenderTool HAS-A EmailConfig.
     */
    public EmailSenderTool() {
        this.config = new EmailConfig();
    }

    @Override
    public String getName() {
        return "email_sender";
    }

    @Override
    public String getDescription() {
        return "Sends a real email via Gmail. Input format: 'to@example.com | Subject | Body'";
    }

    @Override
    public String execute(String input) {
        try {
            // Parse input into an EmailMessage object (Encapsulation + Validation)
            EmailMessage email = new EmailMessage(input);

            // Create authenticated SMTP session using config properties
            Session session = Session.getInstance(config.toMailProperties(),
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(
                            config.getUsername(),
                            config.getPassword()
                        );
                    }
                }
            );

            // Compose the MIME message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(config.getUsername()));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email.getTo()));
            message.setSubject(email.getSubject());
            message.setText(email.getBody());

            // Send it
            Transport.send(message);

            return "Email sent successfully to: " + email.getTo()
                 + " | Subject: " + email.getSubject();

        } catch (IllegalArgumentException e) {
            // Input parsing/validation errors from EmailMessage
            return "Error: " + e.getMessage();
        } catch (AuthenticationFailedException e) {
            return "Error: Gmail authentication failed. Check EMAIL_USER and EMAIL_APP_PASSWORD in .env file.";
        } catch (MessagingException e) {
            return "Error sending email: " + e.getMessage();
        } catch (IllegalStateException e) {
            // Config loading errors (missing env vars)
            return "Error: " + e.getMessage();
        }
    }
}