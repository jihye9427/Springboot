package com.kdt.board.domain.board.dao;

import com.kdt.board.domain.entity.Board;
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
public class BoardDAOImpl implements BoardDAO {

  final private NamedParameterJdbcTemplate template;

  RowMapper<Board> boardRowMapper() {

    return (rs, rowNum) -> {
      Board board = new Board();
      board.setBoardId(rs.getLong("board_id"));
      board.setTitle(rs.getString("title"));
      board.setContent(rs.getString("content"));
      board.setWriter(rs.getString("writer"));
      board.setCreated_date(rs.getTimestamp("created_date"));
      board.setModified_date(rs.getTimestamp("modified_date"));
      return board;
    };
  }

  /**
   * 게시글작성
   * @param board
   * @return 게시글번호
   */
  @Override
  public Long save(Board board) {
    StringBuffer sql = new StringBuffer();
    sql.append(" INSERT INTO board(board_id,title,content,writer) ");
    sql.append("      VALUES (board_seq.NEXTVAL,:title,:content,:writer) ");

    SqlParameterSource param = new BeanPropertySqlParameterSource(board);

    KeyHolder keyHolder = new GeneratedKeyHolder();
    long rows = template.update(sql.toString(), param, keyHolder, new String[]{"board_id"});
    //log.info("rows={}",rows);

    Number pidNumber = (Number) keyHolder.getKeys().get("board_id");
    long pid = pidNumber.longValue();
    return pid;

  }

  /**
   * 게시글목록
   * @return 게시글목록
   */
  @Override
  public List<Board> findAll() {
    StringBuffer sql = new StringBuffer();
    sql.append("   SELECT board_id, title, content, writer, created_date, modified_date  ");
    sql.append("     FROM board ");
    sql.append(" ORDER BY board_id DESC ");

    List<Board> list = template.query(sql.toString(), boardRowMapper());

    return list;
  }

  /**
   * 게시글조회
   * @param id 게시글번호
   * @return 게시글정보
   */
  @Override
  public Optional<Board> findById(Long id) {
    StringBuffer sql = new StringBuffer();
    sql.append(" SELECT board_id, title, content, writer, created_date ");
    sql.append(" FROM board ");
    sql.append(" WHERE board_id = :id ");

    SqlParameterSource param = new MapSqlParameterSource().addValue("id", id);

    Board board = null;
    try {
      board = template.queryForObject(sql.toString(), param, BeanPropertyRowMapper.newInstance(Board.class));
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }

    return Optional.of(board);
  }

  /**
   * 게시글삭제(단건)
   * @param id 게시글번호
   * @return 삭제건수
   */
  public int deleteById(Long id) {
    StringBuffer sql = new StringBuffer();
    sql.append(" DELETE FROM board ");
    sql.append("  WHERE board_id = :id ");

    Map<String, Long> param = Map.of("id",id);
    int rows = template.update(sql.toString(), param);
    return rows;
  }

  /**
   * 상품삭제(여러건)
   * @param ids 게시글번호(여러건)
   * @return 삭제건수
   */
  @Override
  public int deleteByIds(List<Long> ids) {
    StringBuffer sql = new StringBuffer();
    sql.append(" DELETE FROM board ");
    sql.append("  WHERE board_id IN ( :ids ) ");

    Map<String, List<Long>> param = Map.of("ids",ids);
    int rows = template.update(sql.toString(), param);
    return rows;
  }

  /**
   * 게시글수정
   * @param boardId 게시글번호
   * @param board 게시글정보
   * @return 수정건수
   */
  @Override
  public int updateById(Long boardId, Board board) {
    StringBuffer sql = new StringBuffer();
    sql.append(" UPDATE board ");
    sql.append("    SET title = :title, content = :content, writer = :writer, ");
    sql.append(" modified_date = CURRENT_TIMESTAMP ");
    sql.append("  WHERE board_id = :boardId ");

    //수동매핑
    SqlParameterSource param = new MapSqlParameterSource()
        .addValue("title", board.getTitle())
        .addValue("content", board.getContent())
        .addValue("writer", board.getWriter())
        .addValue("created_date", board.getCreated_date())
        .addValue("modified_date", board.getModified_date())
        .addValue("boardId", boardId);

    int rows = template.update(sql.toString(), param);

    return rows;
  }
}