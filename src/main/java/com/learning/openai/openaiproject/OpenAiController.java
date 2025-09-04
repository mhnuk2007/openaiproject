package com.learning.openai.openaiproject;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class OpenAiController {

    private ChatClient chatClient;

    @Autowired
    @Qualifier("openAiEmbeddingModel")
    private EmbeddingModel embeddingModel;

    @GetMapping("api/hello")
    public String greet(){
        return "Hello world!";
    }

    @Autowired
    public OpenAiController(OpenAiChatModel chatModel){
        this.chatClient=ChatClient.create(chatModel);
    }

//    ChatMemory chatMemory = MessageWindowChatMemory.builder().build();
//    public OpenAiController(ChatClient.Builder builder){
//        this.chatClient = builder
//                .defaultAdvisors(MessageChatMemoryAdvisor
//                        .builder(chatMemory)
//                        .build())
//                .build();
//    }


    @GetMapping("/api/{message}")
    public ResponseEntity<String> getAnswer(@PathVariable String message){
        ChatResponse chatResponse = chatClient
                .prompt(message)
                .call()
                .chatResponse();

        System.out.println(chatResponse.getMetadata().getModel());

        String response = chatResponse
                .getResult()
                .getOutput()
                .getText();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/api/recommend")
    public String recommend(@RequestParam String type, @RequestParam String year, @RequestParam String language){

        String tempt = """
                I want to watch a {type} movie tonight with good ratings,
                released in {year},
                and in {language} language.
                Suggest me one specific movie and tell me cast and length of the movie.
                """;

        System.out.println(tempt);

        PromptTemplate promptTemplate = new PromptTemplate(tempt);
        Prompt prompt =promptTemplate.create(Map.of("type", type, "year", year, "language", language));

        System.out.println("Prompt text: " + prompt.getInstructions());

        String response = chatClient
                .prompt(prompt)
                .call()
                .content();
        return response;
    }

    @PostMapping("/api/embedding")
    public float[] embedding(@RequestParam String text){
        return embeddingModel.embed(text);

    }

    @PostMapping("/api/similarity")
    public double getSimilarity(@RequestParam String text1, @RequestParam String text2){

        float[] embedding1 = embeddingModel.embed(text1);
        float[] embedding2 = embeddingModel.embed(text2);


        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (int i = 0; i < embedding1.length; i++) {
            dotProduct += embedding1[i] * embedding2[i];
            norm1 += Math.pow(embedding1[i],2);
            norm2 += Math.pow(embedding2[i],2);
        }

        double response =  (dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2)));

        System.out.println("norm1: " + norm1);
        System.out.println("norm2: " + norm2);
        System.out.println("dotproduct: " + dotProduct);

        System.out.println("response: " + response);

        return response;


    }

}
