package com.kdt.board.domain.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Board {
  private Long boardId;                  //게시글 아이디
  private String title;                  //게시글 제목
  private String content;               //게시글 내용
  private String writer;               //게시글 작성자
  private LocalDateTime created_date;     //게시글 작성날짜
  private LocalDateTime modified_date;   //게시글 수정날짜
}
