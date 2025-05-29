package com.kdt.board.web;

import com.kdt.board.domain.board.svc.BoardSVC;
import com.kdt.board.domain.entity.Board;
import com.kdt.board.web.form.board.SaveForm;
import com.kdt.board.web.form.board.UpdateForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequestMapping("/api/boards")
@RestController
@RequiredArgsConstructor
public class ApiBoardController {

  private final BoardSVC boardSVC;

  // 게시글 작성
  @PostMapping
  public ResponseEntity<Map<String, Object>> save(@Valid @RequestBody SaveForm saveForm, BindingResult bindingResult) {
    Map<String, Object> response = new HashMap<>();
    Map<String, Object> header = new HashMap<>();

    try {
      // 유효성 검증 실패
      if (bindingResult.hasErrors()) {
        Map<String, String> details = new HashMap<>();
        for (FieldError error : bindingResult.getFieldErrors()) {
          details.put(error.getField(), error.getDefaultMessage());
        }

        header.put("rtcd", "E01");
        header.put("rtmsg", "입력값 검증 실패");
        header.put("details", details);
        response.put("header", header);

        return ResponseEntity.badRequest().body(response);
      }

      // 게시글 저장
      Board board = new Board();
      board.setTitle(saveForm.getTitle());
      board.setContent(saveForm.getContent());
      board.setWriter(saveForm.getWriter());

      Long boardId = boardSVC.save(board);

      // 성공 응답
      header.put("rtcd", "S00");
      header.put("rtmsg", "게시글이 성공적으로 등록되었습니다.");
      response.put("header", header);
      response.put("body", Map.of("BoardId", boardId));

      return ResponseEntity.ok(response);

    } catch (Exception e) {
      log.error("게시글 저장 중 오류 발생", e);

      header.put("rtcd", "E99");
      header.put("rtmsg", "서버 오류가 발생했습니다.");
      response.put("header", header);

      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  // 게시글 상세 조회
  @GetMapping("/{id}")
  public ResponseEntity<Map<String, Object>> findById(@PathVariable("id") Long id) {
    Map<String, Object> response = new HashMap<>();
    Map<String, Object> header = new HashMap<>();

    try {
      Optional<Board> optionalBoard = boardSVC.findById(id);

      if (optionalBoard.isEmpty()) {
        header.put("rtcd", "E02");
        header.put("rtmsg", "게시글을 찾을 수 없습니다.");
        response.put("header", header);
        return ResponseEntity.notFound().build();
      }

      Board board = optionalBoard.get();

      // 응답 데이터 구성 (JavaScript에서 기대하는 필드명으로 맞춤)
      Map<String, Object> boardData = new HashMap<>();
      boardData.put("BoardId", board.getBoardId());
      boardData.put("title", board.getTitle());
      boardData.put("content", board.getContent());
      boardData.put("writer", board.getWriter());

      header.put("rtcd", "S00");
      header.put("rtmsg", "게시글 조회 성공");
      response.put("header", header);
      response.put("body", boardData);

      return ResponseEntity.ok(response);

    } catch (Exception e) {
      log.error("게시글 조회 중 오류 발생. id: {}", id, e);

      header.put("rtcd", "E99");
      header.put("rtmsg", "서버 오류가 발생했습니다.");
      response.put("header", header);

      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  // 게시글 수정
  @PatchMapping("/{id}")
  public ResponseEntity<Map<String, Object>> updateById(
      @PathVariable("id") Long id,
      @Valid @RequestBody UpdateForm updateForm,
      BindingResult bindingResult) {

    Map<String, Object> response = new HashMap<>();
    Map<String, Object> header = new HashMap<>();

    try {
      // 유효성 검증 실패
      if (bindingResult.hasErrors()) {
        Map<String, String> details = new HashMap<>();
        for (FieldError error : bindingResult.getFieldErrors()) {
          details.put(error.getField(), error.getDefaultMessage());
        }

        header.put("rtcd", "E01");
        header.put("rtmsg", "입력값 검증 실패");
        header.put("details", details);
        response.put("header", header);

        return ResponseEntity.badRequest().body(response);
      }

      Board board = new Board();
      board.setBoardId(id);
      board.setTitle(updateForm.getTitle());
      board.setContent(updateForm.getContent());
      board.setWriter(updateForm.getWriter());

      int rows = boardSVC.updateById(id, board);

      if (rows > 0) {
        header.put("rtcd", "S00");
        header.put("rtmsg", "게시글이 성공적으로 수정되었습니다.");
        response.put("header", header);
        response.put("body", Map.of("updatedRows", rows));

        return ResponseEntity.ok(response);
      } else {
        header.put("rtcd", "E03");
        header.put("rtmsg", "수정할 게시글을 찾을 수 없습니다.");
        response.put("header", header);

        return ResponseEntity.notFound().build();
      }

    } catch (Exception e) {
      log.error("게시글 수정 중 오류 발생. id: {}", id, e);

      header.put("rtcd", "E99");
      header.put("rtmsg", "서버 오류가 발생했습니다.");
      response.put("header", header);

      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  // 게시글 삭제
  @DeleteMapping("/{id}")
  public ResponseEntity<Map<String, Object>> deleteById(@PathVariable("id") Long id) {
    Map<String, Object> response = new HashMap<>();
    Map<String, Object> header = new HashMap<>();

    try {
      int rows = boardSVC.deleteById(id);

      if (rows > 0) {
        header.put("rtcd", "S00");
        header.put("rtmsg", "게시글이 성공적으로 삭제되었습니다.");
        response.put("header", header);
        response.put("body", Map.of("deletedRows", rows));

        return ResponseEntity.ok(response);
      } else {
        header.put("rtcd", "E04");
        header.put("rtmsg", "삭제할 게시글을 찾을 수 없습니다.");
        response.put("header", header);

        return ResponseEntity.notFound().build();
      }

    } catch (Exception e) {
      log.error("게시글 삭제 중 오류 발생. id: {}", id, e);

      header.put("rtcd", "E99");
      header.put("rtmsg", "서버 오류가 발생했습니다.");
      response.put("header", header);

      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  // 게시글 목록 조회 (페이징)
  @GetMapping("/paging")
  public ResponseEntity<Map<String, Object>> findAllWithPaging(
      @RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
      @RequestParam(value = "numOfRows", defaultValue = "10") int numOfRows) {

    Map<String, Object> response = new HashMap<>();
    Map<String, Object> header = new HashMap<>();

    try {
      // 실제 구현은 BoardSVC에 페이징 메서드가 있어야 합니다.
      // 임시로 전체 조회 후 페이징 처리 (실제로는 DB에서 페이징 처리해야 함)
      List<Board> allBoards = boardSVC.findAll();

      int startIndex = (pageNo - 1) * numOfRows;
      int endIndex = Math.min(startIndex + numOfRows, allBoards.size());

      List<Board> pagedBoards = allBoards.subList(startIndex, Math.max(startIndex, endIndex));

      // JavaScript에서 기대하는 형식으로 변환
      List<Map<String, Object>> boardList = pagedBoards.stream()
          .map(board -> {
            Map<String, Object> boardMap = new HashMap<>();
            boardMap.put("BoardId", board.getBoardId());
            boardMap.put("title", board.getTitle());
            boardMap.put("content", board.getContent());
            boardMap.put("writer", board.getWriter());
            return boardMap;
          })
          .toList();

      header.put("rtcd", "S00");
      header.put("rtmsg", "게시글 목록 조회 성공");
      response.put("header", header);
      response.put("body", boardList);

      return ResponseEntity.ok(response);

    } catch (Exception e) {
      log.error("게시글 목록 조회 중 오류 발생", e);

      header.put("rtcd", "E99");
      header.put("rtmsg", "서버 오류가 발생했습니다.");
      response.put("header", header);

      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  // 전체 게시글 수 조회
  @GetMapping("/totCnt")
  public ResponseEntity<Map<String, Object>> getTotalCount() {
    Map<String, Object> response = new HashMap<>();
    Map<String, Object> header = new HashMap<>();

    try {
      List<Board> allBoards = boardSVC.findAll();
      int totalCount = allBoards.size();

      header.put("rtcd", "S00");
      header.put("rtmsg", "전체 게시글 수 조회 성공");
      response.put("header", header);
      response.put("body", totalCount);

      return ResponseEntity.ok(response);

    } catch (Exception e) {
      log.error("전체 게시글 수 조회 중 오류 발생", e);

      header.put("rtcd", "E99");
      header.put("rtmsg", "서버 오류가 발생했습니다.");
      response.put("header", header);

      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  @GetMapping("/board")
  public String board() {
    // src/main/resources/templates/board.html 을 렌더링
    return "board";
  }

  @GetMapping("/")
  public String home() {
    return "index";  // 홈 템플릿
  }
}