package com.kdt.board.web.form.comments;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class UpdateForm {
  private Long commentId;                 //댓글 고유 아이디

  /** 댓글 작성자 */
  private String writer;

  /** 댓글 내용 */
  private String content;

  private Timestamp createdAt;    //댓글 작성날짜
  private Timestamp modifiedAt;   //댓글 수정날짜
}
