package com.kdt.board.web;

import com.kdt.board.domain.comments.svc.CommentSVC;
import com.kdt.board.domain.entity.Comment;
import com.kdt.board.web.form.comments.SaveForm;
import com.kdt.board.web.form.comments.UpdateForm;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

  final private CommentSVC commentsSVC;

  // 댓글 목록 조회
  @GetMapping("/{boardId}")
  public List<Comment> findAll(@PathVariable Long boardId) {
    return commentsSVC.findAllbyBoardId(boardId);
  }

  // 댓글 작성
  @PostMapping("/{boardId}")
  public ResponseEntity<?> add(
      @PathVariable Long boardId,
      @RequestBody @Valid SaveForm saveForm,
      HttpSession session) {

    String loginMember = (String) session.getAttribute("loginMember"); // 세션에서 사용자 확인
    if (!loginMember.equals(saveForm.getWriter())) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("작성자만 작성 가능");
    }

    Comment comments = new Comment();
    comments.setBoardId(boardId);
    comments.setWriter(saveForm.getWriter());
    comments.setContent(saveForm.getContent());
    commentsSVC.save(comments);

    return ResponseEntity.ok().build();
  }

  // 댓글 수정
  @PatchMapping("/{commentId}")
  public ResponseEntity<?> update(
      @PathVariable Long commentId,
      @RequestBody @Valid UpdateForm updateForm,
      HttpSession session) {

    Optional<Comment> commentOpt = commentsSVC.findById(commentId);
    if (commentOpt.isEmpty()) return ResponseEntity.notFound().build();

    Comment comment = commentOpt.get();
    String loginMember = (String) session.getAttribute("loginMember");

    if (!comment.getWriter().equals(loginMember)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("작성자만 수정 가능");
    }

    comment.setContent(updateForm.getContent());
    commentsSVC.updateById(commentId, comment);
    return ResponseEntity.ok().build();
  }

  // 댓글 삭제
  @DeleteMapping("/{commentId}")
  public ResponseEntity<?> delete(
      @PathVariable Long commentId,
      HttpSession session) {

    Optional<Comment> commentOpt = commentsSVC.findById(commentId);
    if (commentOpt.isEmpty()) return ResponseEntity.notFound().build();

    Comment comment = commentOpt.get();
    String loginMember = (String) session.getAttribute("loginMember");

    if (!comment.getWriter().equals(loginMember)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body("작성자만 삭제 가능");
    }

    commentsSVC.deleteById(commentId);
    return ResponseEntity.ok().build();
  }
}