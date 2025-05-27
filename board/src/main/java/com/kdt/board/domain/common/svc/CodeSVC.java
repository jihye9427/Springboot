package com.kdt.board.domain.common.svc;

import com.kdt.board.domain.common.CodeId;
import com.kdt.board.domain.dito.CodeDTO;

import java.util.List;

public interface CodeSVC {

  /**
   * 코드정보 가져오기
   * @param pcodeId 부모코드
   * @return 하위코드
   */
  List<CodeDTO> getCodes(CodeId pcodeId);

  /**
   * 코드정보 가져오기
   * @return A02 코드 정보
   */
  List<CodeDTO> getA02();
  
}
