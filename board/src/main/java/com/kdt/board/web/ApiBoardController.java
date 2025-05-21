package com.kdt.board.web;

import com.kdt.board.domain.board.svc.BoardSVC;
import com.kdt.board.domain.entity.Board;
import com.kdt.board.web.api.board.SaveApi;
import com.kdt.board.web.api.board.UpdateApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequestMapping("/api/boards")
@RestController // @Controller + @ResponseBody
@RequiredArgsConstructor
public class ApiBoardController {
  private final BoardSVC boardSVC;

  //상품 생성  //POST /boards => POST http://localhost:9080/api/boards
  @PostMapping
  public Board add(@RequestBody SaveApi saveApi) {

    Board board = new Board();
    BeanUtils.copyProperties(saveApi, board);

    Long id = boardSVC.save(board);
    Optional<Board> optionalBoard = boardSVC.findById(id);
    Board findedBoard = optionalBoard.orElseThrow();
    return findedBoard;
  }

  //상품 조회  //GET /Boards/{id} => GET http://localhost:9080/api/Boards/{id}
  @GetMapping("/{id}")
  //@ResponseBody  //응답메세지 바디에 메소드 반환타입의 객체를 json포맷으로 변환하여 반영
  public ResponseEntity<Board> findById(@PathVariable("id") Long id) {
    Optional<Board> optionalBoard = boardSVC.findById(id);
    Board findedBoard = optionalBoard.orElseThrow();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).header("name","홍길동").body(findedBoard);
  }

  //상품 수정 //PATCH /Boards/{id} => PATCH http://localhost:9080/api/Boards/{id}
  @PatchMapping("/{id}")
  public Board updateById(
      @PathVariable("id") Long id,
      @RequestBody UpdateApi updateApi // 요청메세지의 json포맷의 문자열을 자바 객체로 변환
  ) {
    Board Board = new Board();
    BeanUtils.copyProperties(updateApi, Board);
    int updatedRow = boardSVC.updateById(id, Board);
    Optional<Board> optionalBoard = boardSVC.findById(id);
    Board findedBoard = optionalBoard.orElseThrow();
    return findedBoard;
  }

  //상품 삭제 //DELETE /Boards/{id} => DELETE http://localhost:9080/api/Boards/{id}
  @DeleteMapping("/{id}")
  public String deleteById(
      @PathVariable("id") Long id) {

    int deletedRow = boardSVC.deleteById(id);
    return deletedRow == 1 ? "OK" : "NOK";
  }

  //상품 목록      //   GET     /products      =>  GET http://localhost:9080/api/products
  @GetMapping
//  @ResponseBody
  public List<Board> findAll() {
    List<Board> list = boardSVC.findAll();
    return list;
  }
}
