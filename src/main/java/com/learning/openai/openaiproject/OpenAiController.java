package com.learning.openai.openaiproject;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OpenAiController {

    private ChatClient chatClient;

    public OpenAiController(OpenAiChatModel chatModel){
        this.chatClient=ChatClient.create(chatModel);
    }


    @GetMapping("/api/{message}")
    public String getAnswer(@PathVariable String message){
        String response = chatClient
                .prompt(message)
                .call()
                .content();
        return response;
    }

}
