package com.kdt.board.web.api.board;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateApi {
  @NotBlank(message = "제목은 필수입니다")
  @Size(min = 2, max = 50, message = "제목은 2자 이상 50자 이하로 입력해주세요")
  @Pattern(regexp = "^[가-힣a-zA-Z0-9\\s\\-_.!?()\\[\\]]*$",
      message = "제목에는 특수문자가 포함될 수 없습니다 (-, _, ., !, ?, (), [] 제외)")
  private String title;

  @NotBlank(message = "게시글 내용은 필수입니다")
  private String content;

  @NotBlank(message = "작성자는 필수입니다")
  @Size(min = 2, max = 20, message = "작성자명은 2자 이상 20자 이하로 입력해주세요")
  @Pattern(regexp = "^[가-힣a-zA-Z0-9]+$",
      message = "작성자명은 한글, 영문, 숫자만 입력 가능합니다")
  private String writer;
}
