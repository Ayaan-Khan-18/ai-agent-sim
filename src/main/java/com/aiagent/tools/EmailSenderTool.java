package com.aiagent.toolstools;

public class EmailSenderTool implements Tool {
    @Override
    public String getName() {
        return "EmailSenderTool";}
    
    @Override
    public String getDescription() {
        return "A tool to send emails. Input should be in the format: 'to@example.com | Subject | Body'";
    }

    @Override
    public String execute(String input){
        String[] parts = input.split("\\|");
        String to      = parts[0].trim();
        String subject = parts[1].trim();
        String body    = parts[2].trim();

        return "Email sent to: " + to + " | Subject: " + subject;
    }

}       
    

