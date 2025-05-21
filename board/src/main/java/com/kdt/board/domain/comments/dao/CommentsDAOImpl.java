package com.kdt.board.domain.comments.dao;

import com.kdt.board.domain.entity.Comments;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RequiredArgsConstructor
@Repository
public class CommentsDAOImpl implements CommentsDAO {

  final private NamedParameterJdbcTemplate template;

  /**
   * 댓글 등록
   * @param comments
   * @return 댓글번호
   */
  @Override
  public Long save(Comments comments) {
    StringBuffer sql = new StringBuffer();
    sql.append("INSERT INTO comments(comment_id, board_id, parent_id, writer, content, created_at) ");
    sql.append("VALUES (seq_comment_id.NEXTVAL, :board_id, :parent_id, :writer, :content, SYSTIMESTAMP) ");

    SqlParameterSource param = new BeanPropertySqlParameterSource(comments);

    KeyHolder keyHolder = new GeneratedKeyHolder();
    long rows = template.update(sql.toString(),param, keyHolder, new String[]{"comment_id"} );
    return ((Number)keyHolder.getKeys().get("comment_id")).longValue();
  }

  private RowMapper<Comments> doRowMapper(){

    return (rs, rowNum)->{
      Comments comments = new Comments();
      comments.setCommentId(rs.getLong("comment_id"));
      comments.setBoardId(rs.getLong("board_id"));
      comments.setParentId(rs.getLong("parent_id"));
      comments.setWriter(rs.getString("writer"));
      comments.setContent(rs.getString("content"));
      comments.setCreatedAt(rs.getTimestamp("created_at"));
      comments.setUpdatedAt(rs.getTimestamp("updated_at"));
      return comments;
    };
  }

  /**
   * 댓글 목록
   * @return 댓글 목록
   */
  @Override
  public List<Comments> findAll() {
    StringBuffer sql = new StringBuffer();
    sql.append(" SELECT comment_id, board_id, parent_id, writer, content, updated_at ");
    sql.append(" FROM comments ");
    sql.append(" ORDER BY comment_id DESC ");

    List<Comments> list = template.query(sql.toString(), doRowMapper());
    return list;
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
  public int updateById(Long commentId, Comments comments) {
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

}
