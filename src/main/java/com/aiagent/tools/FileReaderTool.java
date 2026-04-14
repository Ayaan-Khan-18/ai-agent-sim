package com.aiagent.tools;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileReaderTool implements Tool {
    @Override
    public String getName() {
        return "file_reader";
    }
    
    @Override
    public String getDescription() {
        return "Reads content of a file. Input format: 'path/to/file.txt'";
    }

    @Override
    public String execute(String input) {
        try{
            String content=Files.readString(Paths.get(input.trim()));
            return content;
        }
        catch(Exception e){
            return "Error reading file: "+e.getMessage();
        }
    }
}