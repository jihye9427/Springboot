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

    // LoginMember 객체로 캐스팅
    LoginMember loginMember = (LoginMember) session.getAttribute("loginMember");
    if (loginMember != null) {
      response.put("success", true);
      response.put("username", loginMember.getNickname()); // 닉네임 사용
      response.put("data", Map.of("username", loginMember.getNickname()));
    } else {
      response.put("success", false);
      response.put("message", "로그인되지 않은 사용자입니다.");
    }

    return ResponseEntity.ok(response);
  }

  // 댓글 목록 조회
  @GetMapping("/{boardId}")
  public ResponseEntity<Map<String, Object>> findAll(@PathVariable Long boardId) {
    try {
      List<Comment> comments = commentsSVC.findAllbyBoardId(boardId);

      Map<String, Object> response = new HashMap<>();
      response.put("success", true);
      response.put("data", comments);
      response.put("count", comments.size());

      return ResponseEntity.ok(response);

    } catch (Exception e) {
      log.error("댓글 목록 조회 중 오류 발생. boardId: {}", boardId, e);

      Map<String, Object> response = new HashMap<>();
      response.put("success", false);
      response.put("message", "댓글 목록을 불러오는 중 오류가 발생했습니다.");

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

    if (bindingResult.hasErrors()) {
      response.put("success", false);
      response.put("message", "입력값이 올바르지 않습니다.");
      response.put("errors", bindingResult.getAllErrors());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 세션에서 로그인 사용자 확인 - LoginMember로 캐스팅
    LoginMember loginMember = (LoginMember) session.getAttribute("loginMember");
    if (loginMember == null) {
      response.put("success", false);
      response.put("message", "로그인이 필요합니다.");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    String currentUsername = loginMember.getNickname(); // 닉네임 사용

    // 작성자 권한 확인 (보안 강화)
    if (!currentUsername.equals(saveForm.getWriter())) {
      response.put("success", false);
      response.put("message", "본인 계정으로만 댓글 작성이 가능합니다.");
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    try {
      // 댓글 저장
      Comment comment = new Comment();
      comment.setBoardId(boardId);
      comment.setWriter(saveForm.getWriter());
      comment.setContent(saveForm.getContent());

      Long commentId = commentsSVC.save(comment);

      // 저장된 댓글 조회
      Optional<Comment> savedCommentOpt = commentsSVC.findById(commentId);
      if (savedCommentOpt.isEmpty()) {
        response.put("success", false);
        response.put("message", "댓글 저장 후 조회에 실패했습니다.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
      }

      Comment savedComment = savedCommentOpt.get();

      response.put("success", true);
      response.put("message", "댓글이 등록되었습니다.");
      response.put("data", savedComment);

      log.info("댓글 등록 완료. commentId: {}, boardId: {}, writer: {}",
          commentId, boardId, currentUsername);

      return ResponseEntity.status(HttpStatus.CREATED).body(response);

    } catch (Exception e) {
      log.error("댓글 등록 중 오류 발생. boardId: {}, writer: {}", boardId, currentUsername, e);

      response.put("success", false);
      response.put("message", "댓글 등록 중 오류가 발생했습니다.");
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

    if (bindingResult.hasErrors()) {
      response.put("success", false);
      response.put("message", "입력값이 올바르지 않습니다.");
      response.put("errors", bindingResult.getAllErrors());
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 세션에서 로그인 사용자 확인 - LoginMember로 캐스팅
    LoginMember loginMember = (LoginMember) session.getAttribute("loginMember");
    if (loginMember == null) {
      response.put("success", false);
      response.put("message", "로그인이 필요합니다.");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    String currentUsername = loginMember.getNickname(); // 닉네임 사용

    try {
      // 댓글 존재 여부 확인
      Optional<Comment> commentOpt = commentsSVC.findById(commentId);
      if (commentOpt.isEmpty()) {
        response.put("success", false);
        response.put("message", "댓글을 찾을 수 없습니다.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
      }

      Comment comment = commentOpt.get();

      // 작성자 권한 확인
      if (!comment.getWriter().equals(currentUsername)) {
        response.put("success", false);
        response.put("message", "작성자만 수정 가능합니다.");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
      }

      // 댓글 수정
      comment.setContent(updateForm.getContent());
      int updateResult = commentsSVC.updateById(commentId, comment);

      if (updateResult > 0) {
        // 수정된 댓글 다시 조회하여 반환
        Optional<Comment> updatedCommentOpt = commentsSVC.findById(commentId);
        Comment updatedComment = updatedCommentOpt.orElse(comment);

        response.put("success", true);
        response.put("message", "댓글이 수정되었습니다.");
        response.put("data", updatedComment);

        log.info("댓글 수정 완료. commentId: {}, writer: {}", commentId, currentUsername);
      } else {
        response.put("success", false);
        response.put("message", "댓글 수정에 실패했습니다.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
      }

      return ResponseEntity.ok(response);

    } catch (Exception e) {
      log.error("댓글 수정 중 오류 발생. commentId: {}, writer: {}", commentId, currentUsername, e);

      response.put("success", false);
      response.put("message", "댓글 수정 중 오류가 발생했습니다.");
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  // 댓글 삭제
  @DeleteMapping("/{commentId}")
  public ResponseEntity<Map<String, Object>> delete(
      @PathVariable Long commentId,
      HttpSession session) {

    Map<String, Object> response = new HashMap<>();

    // 세션에서 로그인 사용자 확인 - LoginMember로 캐스팅
    LoginMember loginMember = (LoginMember) session.getAttribute("loginMember");
    if (loginMember == null) {
      response.put("success", false);
      response.put("message", "로그인이 필요합니다.");
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    String currentUsername = loginMember.getNickname(); // 닉네임 사용

    try {
      // 댓글 존재 여부 확인
      Optional<Comment> commentOpt = commentsSVC.findById(commentId);
      if (commentOpt.isEmpty()) {
        response.put("success", false);
        response.put("message", "댓글을 찾을 수 없습니다.");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
      }

      Comment comment = commentOpt.get();

      // 작성자 권한 확인
      if (!comment.getWriter().equals(currentUsername)) {
        response.put("success", false);
        response.put("message", "작성자만 삭제 가능합니다.");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
      }

      // 댓글 삭제
      int deleteResult = commentsSVC.deleteById(commentId);

      if (deleteResult > 0) {
        response.put("success", true);
        response.put("message", "댓글이 삭제되었습니다.");
        response.put("deletedCommentId", commentId);

        log.info("댓글 삭제 완료. commentId: {}, writer: {}", commentId, currentUsername);
      } else {
        response.put("success", false);
        response.put("message", "댓글 삭제에 실패했습니다.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
      }

      return ResponseEntity.ok(response);

    } catch (Exception e) {
      log.error("댓글 삭제 중 오류 발생. commentId: {}, writer: {}", commentId, currentUsername, e);

      response.put("success", false);
      response.put("message", "댓글 삭제 중 오류가 발생했습니다.");
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }
}