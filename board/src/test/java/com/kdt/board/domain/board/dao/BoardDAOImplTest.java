package com.kdt.board.domain.board.dao;

import com.kdt.board.domain.entity.Board;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

@Slf4j
@SpringBootTest
class BoardDAOImplTest {

  @Autowired
  BoardDAO boardDAO;

  @Test
  @DisplayName("게시글목록")
  void findAll() {
    List<Board> list = boardDAO.findAll();
    log.info("size={}",list.size());
    for (Board board : list) {
      log.info("board={}",board);
    }
  }

  @Test
  @DisplayName("게시글등록")
  void save() {
    Board board = new Board();
    board.setTitle("제목");
    board.setContent("내용");
    board.setWriter("작성자");

    Long pid = boardDAO.save(board);
    log.info("게시글번호={}", pid);
  }

  @Test
  @DisplayName("게시글조회")
  void findById() {
    Long boardId = 1L ;
    Optional<Board> optionalBoard = boardDAO.findById(boardId);
    Board findedBoard = optionalBoard.orElseThrow();// 값이 없으면 예외 발생
    log.info("findedBoard={}", findedBoard);
  }

  @Test
  @DisplayName("게시글삭제(단건)")
  void deleteById() {
    Long id = 21L;
    int rows = boardDAO.deleteById(id);
    log.info("rows={}",rows);
    Assertions.assertThat(rows).isEqualTo(1);
  }

  @Test
  @DisplayName("게시글삭제(여러건)")
  void deleteByIds() {
    List<Long> ids = List.of(2L,3L);
    int rows = boardDAO.deleteByIds(ids);
    Assertions.assertThat(rows).isEqualTo(2);
  }

  @Test
  @DisplayName("게시글수정")
  void updateById() {
    Long id = 22L;
    Board board = new Board();
    board.setTitle("제목");
    board.setContent("내용");
    board.setWriter("작성자");

    int rows = boardDAO.updateById(id, board);

    Optional<Board> optBoard = boardDAO.findById(id);
    Board modifiedBoard = optBoard.orElseThrow();

    Assertions.assertThat(modifiedBoard.getTitle()).isEqualTo("제목");
    Assertions.assertThat(modifiedBoard.getContent()).isEqualTo("내용");
    Assertions.assertThat(modifiedBoard.getWriter()).isEqualTo("작성자");
  }
}