package com.kdt.board.domain.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Comment {
  private Long commentId;                //댓글 번호
  private Long boardId;                 //(댓글이 속한) 게시글 아이디
  private Long parentId;               //대댓글의 아이디
  private String writer;              //댓글 작성자
  private String content;            //댓글 내용
  private LocalDateTime createdAt;     //게시글 작성날짜
  private LocalDateTime updatedAt;    //게시글 수정날짜
}
