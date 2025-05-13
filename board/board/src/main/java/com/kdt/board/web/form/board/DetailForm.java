package com.kdt.board.web.form.board;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class DetailForm {
  private Long boardId;
  private String title;
  private String content;
  private Timestamp created_date;
  private Timestamp modified_date;
}
