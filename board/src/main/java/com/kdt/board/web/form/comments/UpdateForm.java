package com.kdt.board.web.form.comments;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateForm {
  private Long commentId;                 //댓글 고유 아이디

  /** 댓글 작성자 */
  private String writer;

  /** 댓글 내용 */
  private String content;

  private LocalDateTime createdAt;    //댓글 작성날짜
  private LocalDateTime modifiedAt;   //댓글 수정날짜
}
