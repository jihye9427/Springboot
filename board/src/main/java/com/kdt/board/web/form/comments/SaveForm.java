package com.kdt.board.web.form.comments;

import lombok.Data;

@Data
public class SaveForm {

  /** 댓글 작성자 */
  private String writer;

  /** 댓글 내용 */
  private String content;

}

