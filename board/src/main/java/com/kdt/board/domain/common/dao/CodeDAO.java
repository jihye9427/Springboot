package com.kdt.board.domain.common.dao;

import com.kdt.board.domain.common.CodeId;
import com.kdt.board.domain.dito.CodeDTO;

import java.util.List;

public interface CodeDAO {
  List<CodeDTO> loadCodes(CodeId pcodeId);
}
