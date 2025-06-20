package com.kdt.board.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class OpenAiChatController {
  private final ChatClient chatClient;

  @GetMapping("/openAIChat")
  public String chat() {
    return chatClient.prompt()
        .user("OpenAI에 대한 소개글을 작성해줘")
        .call()
        .content();
  }
}
