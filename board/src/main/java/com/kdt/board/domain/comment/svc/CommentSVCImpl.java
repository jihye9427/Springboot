package com.kdt.board.domain.comment.svc;

import com.kdt.board.domain.comment.dao.CommentDAO;
import com.kdt.board.domain.entity.Comment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentSVCImpl implements CommentSVC {

  final private CommentDAO commentDAO;

  @Override
  public Long save(Comment comments) {
    return commentDAO.save(comments);
  }

  @Override
  public List<Comment> findAll() {
    return commentDAO.findAll();
  }

  @Override
  public Optional<Comment> findById(Long id) {
    return commentDAO.findById(id);
  }

  @Override
  public int deleteById(Long id) {
    return commentDAO.deleteById(id);
  }

  @Override
  public int updateById(Long commentId, Comment comments) {
    return commentDAO.updateById(commentId, comments);
  }

  @Override
  public List<Comment> findAllbyBoardId(Long boardId) {
    return commentDAO.findAllbyBoardId(boardId);
  }
}