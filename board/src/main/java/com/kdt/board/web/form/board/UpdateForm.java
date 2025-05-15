package com.kdt.board.web.form.board;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class UpdateForm {
  private Long boardId;                 //게시글 아이디

  /** 게시글 제목 */
  @NotBlank(message = "제목은 필수입니다.")
  private String title;

  /** 게시글 내용 */
  @NotBlank(message = "내용은 필수입니다.")
  private String content;

  /** 게시글 작성자 */
  @NotBlank(message = "작성자는 필수입니다.")
  private String writer;

  private Timestamp created_date;    //게시글 작성날짜
  private Timestamp modified_date;   //게시글 수정날짜
}
