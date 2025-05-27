package com.kdt.board.domain.common.svc;

import com.kdt.board.domain.common.CodeId;
import com.kdt.board.domain.common.dao.CodeDAO;
import com.kdt.board.domain.dito.CodeDTO;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CodeSVCImpl implements CodeSVC {

  private final CodeDAO codeDAO;
  private List<CodeDTO> a02;

  @Override
  public List<CodeDTO> getCodes(CodeId pcodeId) {
    return codeDAO.loadCodes(pcodeId);
  }

  @PostConstruct
  private void initA02Code() {
    log.info("A02 코드 초기화 수행됨!");
    a02 = codeDAO.loadCodes(CodeId.A02);
  }

  public List<CodeDTO> getA02() {
    return a02;
  }
}
