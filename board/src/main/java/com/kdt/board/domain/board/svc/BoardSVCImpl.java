package com.kdt.board.domain.board.svc;

import com.kdt.board.domain.board.dao.BoardDAO;
import com.kdt.board.domain.entity.Board;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardSVCImpl implements BoardSVC {

  final private BoardDAO boardDAO;

  //게시글 작성
  public Long save(Board board) {
    return boardDAO.save(board);
  }

  //게시글 목록
  public List<Board> findAll() {
    return boardDAO.findAll();
  }

  //게시글 상세조회
  public Optional<Board> findById(Long id) {
    return boardDAO.findById(id);
  }

  //게시글 삭제(단건)
  public int deleteById(Long id) {
    return boardDAO.deleteById(id);
  }

  //게시글 삭제(여러건)
  public int deleteByIds(List<Long> ids) {
    return boardDAO.deleteByIds(ids);
  }

  //게시글 수정
  public int updateById(Long boardId, Board board) {
    return boardDAO.updateById(boardId, board);
  }
}
