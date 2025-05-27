package com.kdt.board.web.api.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SaveApi {
  @NotBlank(message = "작성자는 필수입니다")
  private String writer;

  @NotBlank(message = "댓글 내용은 필수입니다")
  @Size(max = 500, message = "댓글은 500자를 초과할 수 없습니다")
  private String content;

  // 대댓글 기능을 위한 필드 (선택사항)
  private Long parentId;
}
