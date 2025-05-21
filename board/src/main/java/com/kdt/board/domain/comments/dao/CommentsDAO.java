package com.kdt.board.domain.comments.dao;

import com.kdt.board.domain.entity.Comments;

import java.util.List;

public interface CommentsDAO {

  //댓글 등록
  Long save(Comments comments);

  //댓글 목록
  List<Comments> findAll();

  //댓글 삭제(단건)
  int deleteById(Long id);

  //게시글 수정
  int updateById(Long commentId, Comments comments);
}
