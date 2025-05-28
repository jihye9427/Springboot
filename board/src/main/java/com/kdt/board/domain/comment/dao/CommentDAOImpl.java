package com.kdt.board.domain.comment.dao;

import com.kdt.board.domain.entity.Comment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
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
    sql.append("VALUES (comments_comment_id_seq.NEXTVAL, :boardId, :parentId, :writer, :content, SYSTIMESTAMP) ");

    SqlParameterSource param = new BeanPropertySqlParameterSource(comments);

    KeyHolder keyHolder = new GeneratedKeyHolder();
    template.update(sql.toString(), param, keyHolder, new String[]{"comment_id"});
    return ((Number)keyHolder.getKeys().get("comment_id")).longValue();
  }

  private RowMapper<Comment> doRowMapper(){
    return (rs, rowNum) -> {
      Comment comments = new Comment();
      comments.setCommentId(rs.getLong("comment_id"));
      comments.setBoardId(rs.getLong("board_id"));

      long parentId = rs.getLong("parent_id");
      if (!rs.wasNull()) {
        comments.setParentId(parentId);
      }

      comments.setWriter(rs.getString("writer"));
      comments.setContent(rs.getString("content"));
      comments.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

      if (rs.getTimestamp("updated_at") != null) {
        comments.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
      }

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
    sql.append("SELECT comment_id, board_id, parent_id, writer, content, created_at, updated_at ");
    sql.append("FROM comments ");
    sql.append("ORDER BY comment_id DESC ");

    return template.query(sql.toString(), doRowMapper());
  }

  /**
   * 댓글 상세 조회
   * @param id 댓글번호
   * @return 댓글정보
   */
  @Override
  public Optional<Comment> findById(Long id) {
    StringBuffer sql = new StringBuffer();
    sql.append("SELECT comment_id, board_id, parent_id, writer, content, created_at, updated_at ");
    sql.append("FROM comments ");
    sql.append("WHERE comment_id = :id ");

    SqlParameterSource param = new MapSqlParameterSource().addValue("id", id);

    try {
      Comment comment = template.queryForObject(sql.toString(), param, doRowMapper());
      return Optional.of(comment);
    } catch (EmptyResultDataAccessException e) {
      log.debug("댓글을 찾을 수 없습니다. commentId: {}", id);
      return Optional.empty();
    }
  }

  /**
   * 댓글 삭제
   * @param id 댓글번호
   * @return 삭제건수
   */
  @Override
  public int deleteById(Long id) {
    StringBuffer sql = new StringBuffer();
    sql.append("DELETE FROM comments ");
    sql.append("WHERE comment_id = :id ");

    Map<String, Long> param = Map.of("id", id);
    int rows = template.update(sql.toString(), param);

    log.debug("댓글 삭제 완료. commentId: {}, 삭제된 행 수: {}", id, rows);
    return rows;
  }

  /**
   * 댓글 수정
   * @param commentId 댓글번호
   * @param comments 댓글정보
   * @return
   */
  @Override
  public int updateById(Long commentId, Comment comments) {
    StringBuffer sql = new StringBuffer();
    sql.append("UPDATE comments ");
    sql.append("SET content = :content, updated_at = CURRENT_TIMESTAMP ");
    sql.append("WHERE comment_id = :commentId ");

    SqlParameterSource param = new MapSqlParameterSource()
        .addValue("commentId", commentId)
        .addValue("content", comments.getContent());

    int rows = template.update(sql.toString(), param);

    log.debug("댓글 수정 완료. commentId: {}, 수정된 행 수: {}", commentId, rows);
    return rows;
  }

  /**
   * 특정 게시글에 달린 목록조회
   * @param boardId 게시글번호
   * @return 해당 게시글에 달린 댓글 리스트
   */
  @Override
  public List<Comment> findAllbyBoardId(Long boardId) {
    StringBuffer sql = new StringBuffer();
    sql.append("SELECT comment_id, board_id, parent_id, writer, content, created_at, updated_at ");
    sql.append("FROM comments ");
    sql.append("WHERE board_id = :boardId ");
    sql.append("ORDER BY comment_id ASC ");

    SqlParameterSource param = new MapSqlParameterSource().addValue("boardId", boardId);
    return template.query(sql.toString(), param, doRowMapper());
  }
}
