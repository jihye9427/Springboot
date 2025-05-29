package com.kdt.board.web;

import com.kdt.board.domain.comment.svc.CommentSVC;
import com.kdt.board.domain.entity.Comment;
import com.kdt.board.web.form.comments.SaveForm;
import com.kdt.board.web.form.comments.UpdateForm;
import com.kdt.board.web.form.login.LoginMember;
import jakarta.servlet.http.HttpSession;
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
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class ApiCommentController {

  private final CommentSVC commentsSVC;

  // 현재 로그인 사용자 정보 조회 API
  @GetMapping("/current-user")
  public ResponseEntity<Map<String, Object>> getCurrentUser(HttpSession session) {
    Map<String, Object> response = new HashMap<>();
    Map<String, Object> header = new HashMap<>();

    LoginMember loginMember = (LoginMember) session.getAttribute("loginMember");
    if (loginMember != null) {
      header.put("rtcd", "S00");
      header.put("rtmsg", "로그인 사용자 조회 성공");
      response.put("header", header);
      response.put("body", Map.of("username", loginMember.getNickname()));
    } else {
      header.put("rtcd", "E01");
      header.put("rtmsg", "로그인되지 않은 사용자입니다.");
      response.put("header", header);
    }

    return ResponseEntity.ok(response);
  }

  // 프론트엔드 호환성을 위한 간단한 댓글 목록 조회 API 추가
  @GetMapping("/{boardId}")
  public ResponseEntity<Map<String, Object>> findAll(@PathVariable Long boardId) {
    Map<String, Object> response = new HashMap<>();
    Map<String, Object> header = new HashMap<>();

    try {
      List<Comment> allComments = commentsSVC.findAllbyBoardId(boardId);

      header.put("rtcd", "S00");
      header.put("rtmsg", "댓글 목록 조회 성공");
      response.put("header", header);
      response.put("body", allComments);

      return ResponseEntity.ok(response);

    } catch (Exception e) {
      log.error("댓글 목록 조회 중 오류 발생. boardId: {}", boardId, e);

      header.put("rtcd", "E99");
      header.put("rtmsg", "댓글 목록을 불러오는 중 오류가 발생했습니다.");
      response.put("header", header);

      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  // 댓글 목록 조회 (페이징)
  @GetMapping("/{boardId}/paging")
  public ResponseEntity<Map<String, Object>> findAllWithPaging(
      @PathVariable Long boardId,
      @RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
      @RequestParam(value = "numOfRows", defaultValue = "10") int numOfRows) {

    Map<String, Object> response = new HashMap<>();
    Map<String, Object> header = new HashMap<>();

    try {
      // 전체 댓글 조회 후 페이징 처리 (실제로는 DB에서 페이징 처리 권장)
      List<Comment> allComments = commentsSVC.findAllbyBoardId(boardId);

      int startIndex = (pageNo - 1) * numOfRows;
      int endIndex = Math.min(startIndex + numOfRows, allComments.size());

      List<Comment> pagedComments = allComments.subList(startIndex, Math.max(startIndex, endIndex));

      header.put("rtcd", "S00");
      header.put("rtmsg", "댓글 목록 조회 성공");
      response.put("header", header);
      response.put("body", pagedComments);

      return ResponseEntity.ok(response);

    } catch (Exception e) {
      log.error("댓글 목록 조회 중 오류 발생. boardId: {}", boardId, e);

      header.put("rtcd", "E99");
      header.put("rtmsg", "댓글 목록을 불러오는 중 오류가 발생했습니다.");
      response.put("header", header);

      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  // 전체 댓글 수 조회
  @GetMapping("/{boardId}/totCnt")
  public ResponseEntity<Map<String, Object>> getTotalCount(@PathVariable Long boardId) {
    Map<String, Object> response = new HashMap<>();
    Map<String, Object> header = new HashMap<>();

    try {
      List<Comment> allComments = commentsSVC.findAllbyBoardId(boardId);
      int totalCount = allComments.size();

      header.put("rtcd", "S00");
      header.put("rtmsg", "전체 댓글 수 조회 성공");
      response.put("header", header);
      response.put("body", totalCount);

      return ResponseEntity.ok(response);

    } catch (Exception e) {
      log.error("전체 댓글 수 조회 중 오류 발생. boardId: {}", boardId, e);

      header.put("rtcd", "E99");
      header.put("rtmsg", "서버 오류가 발생했습니다.");
      response.put("header", header);

      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  // 댓글 작성
  @PostMapping("/{boardId}")
  public ResponseEntity<Map<String, Object>> add(
      @PathVariable Long boardId,
      @RequestBody @Valid SaveForm saveForm,
      BindingResult bindingResult,
      HttpSession session) {

    Map<String, Object> response = new HashMap<>();
    Map<String, Object> header = new HashMap<>();

    if (bindingResult.hasErrors()) {
      Map<String, String> details = new HashMap<>();
      for (FieldError error : bindingResult.getFieldErrors()) {
        details.put(error.getField(), error.getDefaultMessage());
      }

      header.put("rtcd", "E01");
      header.put("rtmsg", "입력값 검증 실패");
      header.put("details", details);
      response.put("header", header);

      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 세션에서 로그인 사용자 확인
    LoginMember loginMember = (LoginMember) session.getAttribute("loginMember");
    if (loginMember == null) {
      header.put("rtcd", "E02");
      header.put("rtmsg", "로그인이 필요합니다.");
      response.put("header", header);
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    String currentUsername = loginMember.getNickname();

    // 작성자 권한 확인 (보안 강화)
    if (!currentUsername.equals(saveForm.getWriter())) {
      header.put("rtcd", "E03");
      header.put("rtmsg", "본인 계정으로만 댓글 작성이 가능합니다.");
      response.put("header", header);
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    try {
      // 댓글 저장 (폼에서 받은 writer 사용)
      Comment comment = new Comment();
      comment.setBoardId(boardId);
      comment.setWriter(saveForm.getWriter());
      comment.setContent(saveForm.getContent());

      Long commentId = commentsSVC.save(comment);

      Optional<Comment> savedCommentOpt = commentsSVC.findById(commentId);
      if (savedCommentOpt.isEmpty()) {
        header.put("rtcd", "E99");
        header.put("rtmsg", "댓글 저장 후 조회에 실패했습니다.");
        response.put("header", header);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
      }

      Comment savedComment = savedCommentOpt.get();

      header.put("rtcd", "S00");
      header.put("rtmsg", "댓글이 등록되었습니다.");
      response.put("header", header);
      response.put("body", Map.of("commentId", commentId));

      log.info("댓글 등록 완료. commentId: {}, boardId: {}, writer: {}",
          commentId, boardId, saveForm.getWriter());

      return ResponseEntity.status(HttpStatus.CREATED).body(response);

    } catch (Exception e) {
      log.error("댓글 등록 중 오류 발생. boardId: {}, writer: {}", boardId, saveForm.getWriter(), e);

      header.put("rtcd", "E99");
      header.put("rtmsg", "댓글 등록 중 오류가 발생했습니다.");
      response.put("header", header);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  // 댓글 수정
  @PatchMapping("/{commentId}")
  public ResponseEntity<Map<String, Object>> update(
      @PathVariable Long commentId,
      @RequestBody @Valid UpdateForm updateForm,
      BindingResult bindingResult,
      HttpSession session) {

    Map<String, Object> response = new HashMap<>();
    Map<String, Object> header = new HashMap<>();

    if (bindingResult.hasErrors()) {
      Map<String, String> details = new HashMap<>();
      for (FieldError error : bindingResult.getFieldErrors()) {
        details.put(error.getField(), error.getDefaultMessage());
      }

      header.put("rtcd", "E01");
      header.put("rtmsg", "입력값 검증 실패");
      header.put("details", details);
      response.put("header", header);

      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 세션에서 로그인 사용자 확인
    LoginMember loginMember = (LoginMember) session.getAttribute("loginMember");
    if (loginMember == null) {
      header.put("rtcd", "E02");
      header.put("rtmsg", "로그인이 필요합니다.");
      response.put("header", header);
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    String currentUsername = loginMember.getNickname();

    try {
      // 댓글 존재 여부 확인
      Optional<Comment> commentOpt = commentsSVC.findById(commentId);
      if (commentOpt.isEmpty()) {
        header.put("rtcd", "E03");
        header.put("rtmsg", "댓글을 찾을 수 없습니다.");
        response.put("header", header);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
      }

      Comment comment = commentOpt.get();

      // 작성자 권한 확인 - 핵심 보안 검증
      if (!comment.getWriter().equals(currentUsername)) {
        header.put("rtcd", "E04");
        header.put("rtmsg", "작성자만 수정 가능합니다.");
        response.put("header", header);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
      }

      // 댓글 수정
      comment.setContent(updateForm.getContent());
      int updateResult = commentsSVC.updateById(commentId, comment);

      if (updateResult > 0) {
        header.put("rtcd", "S00");
        header.put("rtmsg", "댓글이 수정되었습니다.");
        response.put("header", header);
        response.put("body", Map.of("updatedRows", updateResult));

        log.info("댓글 수정 완료. commentId: {}, writer: {}", commentId, currentUsername);
      } else {
        header.put("rtcd", "E99");
        header.put("rtmsg", "댓글 수정에 실패했습니다.");
        response.put("header", header);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
      }

      return ResponseEntity.ok(response);

    } catch (Exception e) {
      log.error("댓글 수정 중 오류 발생. commentId: {}, writer: {}", commentId, currentUsername, e);

      header.put("rtcd", "E99");
      header.put("rtmsg", "댓글 수정 중 오류가 발생했습니다.");
      response.put("header", header);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  // 댓글 삭제
  @DeleteMapping("/{commentId}")
  public ResponseEntity<Map<String, Object>> delete(
      @PathVariable Long commentId,
      HttpSession session) {

    Map<String, Object> response = new HashMap<>();
    Map<String, Object> header = new HashMap<>();

    // 세션에서 로그인 사용자 확인
    LoginMember loginMember = (LoginMember) session.getAttribute("loginMember");
    if (loginMember == null) {
      header.put("rtcd", "E02");
      header.put("rtmsg", "로그인이 필요합니다.");
      response.put("header", header);
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    String currentUsername = loginMember.getNickname();

    try {
      // 댓글 존재 여부 확인
      Optional<Comment> commentOpt = commentsSVC.findById(commentId);
      if (commentOpt.isEmpty()) {
        header.put("rtcd", "E03");
        header.put("rtmsg", "댓글을 찾을 수 없습니다.");
        response.put("header", header);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
      }

      Comment comment = commentOpt.get();

      // 작성자 권한 확인 - 핵심 보안 검증
      if (!comment.getWriter().equals(currentUsername)) {
        header.put("rtcd", "E04");
        header.put("rtmsg", "작성자만 삭제 가능합니다.");
        response.put("header", header);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
      }

      // 댓글 삭제
      int deleteResult = commentsSVC.deleteById(commentId);

      if (deleteResult > 0) {
        header.put("rtcd", "S00");
        header.put("rtmsg", "댓글이 삭제되었습니다.");
        response.put("header", header);
        response.put("body", Map.of("deletedRows", deleteResult));

        log.info("댓글 삭제 완료. commentId: {}, writer: {}", commentId, currentUsername);
      } else {
        header.put("rtcd", "E99");
        header.put("rtmsg", "댓글 삭제에 실패했습니다.");
        response.put("header", header);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
      }

      return ResponseEntity.ok(response);

    } catch (Exception e) {
      log.error("댓글 삭제 중 오류 발생. commentId: {}, writer: {}", commentId, currentUsername, e);

      header.put("rtcd", "E99");
      header.put("rtmsg", "댓글 삭제 중 오류가 발생했습니다.");
      response.put("header", header);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }
}