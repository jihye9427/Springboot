package com.kdt.board.web.api.board;

import lombok.Data;

@Data
public class UpdateApi {
  private String title;
  private String content;
  private String writer;
}
