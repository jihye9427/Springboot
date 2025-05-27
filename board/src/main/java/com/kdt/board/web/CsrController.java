package com.kdt.board.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/csr")
public class CsrController {

  @GetMapping("/boards")
  public String boards() {
    log.info("boards() 호출됨");
    return "csr/board/board";
  }
}
