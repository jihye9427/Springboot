package com.kdt.board.domain.comments.dao;

import com.kdt.board.domain.entity.Comment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
public class CommentDAOImpl implements CommentDAO {

  final private NamedParameterJdbcTemplate template;

  @Override
  public Long save(Comment comments) {
    StringBuffer sql = new StringBuffer();
    sql.append("INSERT INTO comments(comment_id, board_id, parent_id, writer, content, created_at) ");
    sql.append("VALUES (seq_comment_id.NEXTVAL, :board_id, :parent_id, :writer, :content, SYSTIMESTAMP) ");

    SqlParameterSource param = new BeanPropertySqlParameterSource(comments);

    KeyHolder keyHolder = new GeneratedKeyHolder();
    long rows = template.update(sql.toString(),param, keyHolder, new String[]{"comment_id"} );
    return ((Number)keyHolder.getKeys().get("comment_id")).longValue();
  }

  private RowMapper<Comment> doRowMapper(){

    return (rs, rowNum)->{
      Comment comments = new Comment();
      comments.setCommentId(rs.getLong("comment_id"));
      comments.setBoardId(rs.getLong("board_id"));
      comments.setParentId(rs.getLong("parent_id"));
      comments.setWriter(rs.getString("writer"));
      comments.setContent(rs.getString("content"));
      comments.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
      comments.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
      return comments;
    };
  }

  /**
   * 댓글 목록 조회
   * @return 댓글 목록
   */
  @Override
  public List<Comment> findAll() {
    StringBuffer sql = new StringBuffer();
    sql.append(" SELECT comment_id, board_id, parent_id, writer, content, updated_at ");
    sql.append(" FROM comments ");
    sql.append(" ORDER BY comment_id DESC ");

    List<Comment> list = template.query(sql.toString(), doRowMapper());
    return list;
  }

  /**
   * 댓글 상세 조회
   * @param id
   * @return
   */
  @Override
  public Optional<Comment> findById(Long id) {
    StringBuffer sql = new StringBuffer();
    sql.append(" SELECT comment_id, writer, content, updated_at ");
    sql.append(" FROM comments ");
    sql.append(" WHERE comment_id = :id ");

    SqlParameterSource param = new MapSqlParameterSource().addValue("id", id);

    Comment comments = null;
    try {
      comments = template.queryForObject(sql.toString(), param, BeanPropertyRowMapper.newInstance(Comment.class));
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }

    return Optional.of(comments);
  }

  /**
   * 댓글 삭제
   * @param id 댓글번호
   * @return 삭제건수
   */
  @Override
  public int deleteById(Long id) {
    StringBuffer sql = new StringBuffer();
    sql.append(" DELETE FROM comments ");
    sql.append(" WHERE comment_id = :id ");

    Map<String, Long> param = Map.of("id", id);
    int rows = template.update(sql.toString(), param);
    return rows;
  }

  /**
   * 댓글 수정
   * @param commentId 댓글번호
   * @param comments 댓글정보
   * @return 수정건수
   */
  @Override
  public int updateById(Long commentId, Comment comments) {
    StringBuffer sql = new StringBuffer();
    sql.append(" UPDATE comments ");
    sql.append(" SET writer = :writer, content = :content, ");
    sql.append("     updated_at = CURRENT_TIMESTAMP ");
    sql.append(" WHERE comment_id = :commentId ");

    SqlParameterSource param = new MapSqlParameterSource()
        .addValue("commentId", comments.getCommentId())
        .addValue("boardId", comments.getBoardId())
        .addValue("parentId", comments.getParentId())
        .addValue("writer", comments.getWriter())
        .addValue("content", comments.getContent())
        .addValue("createdAt", comments.getCreatedAt())
        .addValue("updatedAt", comments.getUpdatedAt());

    int rows = template.update(sql.toString(), param);

    return rows;
  }

  /**
   * 특정 게시글에 달린 댓글 목록조회
   * @param boardId 게시글 id
   * @return 해당 게시글에 달린 댓글 리스트
   */
  @Override
  public List<Comment> findAllbyBoardId(Long boardId) {
    StringBuffer sql = new StringBuffer();
    sql.append(" SELECT comment_id, board_id, parent_id, writer, content, created_at, updated_at ");
    sql.append(" FROM comments ");
    sql.append(" WHERE board_id = :boardId ");
    sql.append(" ORDER BY comment_id DESC ");

    SqlParameterSource param = new MapSqlParameterSource().addValue("boardId", boardId);
    return template.query(sql.toString(), param, doRowMapper());
  }
}
