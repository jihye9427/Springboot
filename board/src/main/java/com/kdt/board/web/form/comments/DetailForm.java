package com.kdt.board.web.form.comments;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DetailForm {
  private Long commentId;                //댓글 고유 아이디
  private Long boardId;                 //(댓글이 속한) 게시글 아이디
  private Long parentId;               //대댓글의 아이디
  private String writer;              //댓글 작성자
  private String content;            //댓글 내용
  private LocalDateTime createdAt;     //댓글 작성날짜
  private LocalDateTime updatedAt;    //댓글 수정날짜
}
