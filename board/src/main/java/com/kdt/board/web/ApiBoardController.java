package com.kdt.board.web;

import com.kdt.board.domain.board.svc.BoardSVC;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/api/boards")
@RestController // @Controller + @ResponseBody
@RequiredArgsConstructor
public class ApiBoardController {

  private final BoardSVC boardSVC;
  
}
