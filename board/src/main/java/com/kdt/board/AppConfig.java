package com.kdt.board;

import com.kdt.board.web.interceptor.ExecutionTimeInterceptor;
import com.kdt.board.web.interceptor.LoginCheckInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class AppConfig implements WebMvcConfigurer {

  private final LoginCheckInterceptor loginCheckInterceptor;
  private final ExecutionTimeInterceptor executionTimeInterceptor;

  @Bean
  public ChatClient openAIChatClient(OpenAiChatModel chatModel) {
    return ChatClient.create(chatModel);
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {

    registry.addInterceptor(loginCheckInterceptor)
        .order(2)
        .addPathPatterns("/**")  //루트부터 하위경로 모두 인터셉터 대상으로 포함
        .excludePathPatterns(
            "/",   //초기화면
            "/login",          //로그인화면
            "/logout",         //로그아웃
            "/member/join",   //회원가입
            "/board/**"        //게시판
        );
    registry.addInterceptor(executionTimeInterceptor)
        .order(1);
  }
}
