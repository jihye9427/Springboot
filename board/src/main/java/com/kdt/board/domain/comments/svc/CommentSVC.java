package com.kdt.board.domain.comments.svc;

import com.kdt.board.domain.entity.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentSVC {

  //댓글 작성
  Long save(Comment comments);

  //댓글 목록조회
  List<Comment> findAll();

  //댓글 상세조회
  Optional<Comment> findById(Long id);

  //댓글 삭제
  int deleteById(Long id);

  //댓글 수정
  int updateById(Long commentId, Comment comments);
  
  //특정 게시글에 달린 댓글 목록조회
  List<Comment> findAllbyBoardId(Long boardId);
}
