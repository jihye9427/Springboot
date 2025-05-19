package com.kdt.board.web.form.board;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class DetailForm {
  private Long boardId;                   //게시글 아이디
  private String title;                  //게시글 제목
  private String content;               //게시글 내용
  private String writer;               //게시글 작성자
  private Timestamp created_date;     //게시글 작성날짜
  private Timestamp modified_date;   //게시글 수정날짜
}
