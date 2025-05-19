package com.kdt.board.web.form.board;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SaveForm {

  /** 게시글 제목 */
  @NotBlank(message = "제목은 필수입니다.")
  private String title;

  /** 게시글 본문 내용 */
  @NotBlank(message = "내용은 필수입니다.")
  private String content;

  /** 게시글 작성자 */
  @NotBlank(message = "작성자는 필수입니다.")
  private String writer;
}
