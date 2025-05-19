package com.kdt.board.domain.board.dao;

import com.kdt.board.domain.entity.Board;

import java.util.List;
import java.util.Optional;

public interface BoardDAO {

  //게시글 작성
  Long save(Board board);

  //게시글 목록
  List<Board> findAll();

  //게시글 상세조회
  Optional<Board> findById(Long id);

  //게시글 삭제(단건)
  int deleteById(Long id);

  //게시글 삭제(여러건)
  int deleteByIds(List<Long> ids);

  //게시글 수정
  int updateById(Long boardId, Board board);
}