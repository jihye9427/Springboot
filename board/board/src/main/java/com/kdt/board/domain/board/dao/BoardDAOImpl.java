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
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Repository
class BoardDAOImpl implements BoardDAO {

  final private NamedParameterJdbcTemplate template;

  RowMapper<Board> boardRowMapper(){

    return (rs, rowNum)->{
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

  //상품등록
  @Override
  public Long save(Board board) {
    StringBuffer sql = new StringBuffer();
    sql.append(" INSERT INTO board ");
    sql.append(" VALUES (board_seq.NEXTVAL, '가입인사', '안녕하세요, 잘 부탁 드립니다.', '밀물', SYSDATE, SYSDATE) ");

    SqlParameterSource param = new BeanPropertySqlParameterSource(board);

    KeyHolder keyHolder = new GeneratedKeyHolder();
    long rows = template.update(sql.toString(),param, keyHolder, new String[]{"board_id"} );
    //log.info("rows={}",rows);

    Number pidNumber = (Number)keyHolder.getKeys().get("board_id");
    long pid = pidNumber.longValue();
    return pid;

  }

  //상품목록
  @Override
  public List<Board> findAll() {
    StringBuffer sql = new StringBuffer();
    sql.append(" INSERT INTO board VALUES (board_seq.NEXTVAL, '가입인사', '안녕하세요, 잘 부탁 드립니다.', '밀물', SYSDATE, SYSDATE) ");
    sql.append(" SELECT board_id, title, content, writer, ");
    sql.append(" TO_CHAR(created_date, 'YYYY-MM-DD HH24:MI:SS') AS created_date, ");
    sql.append(" TO_CHAR(modified_date, 'YYYY-MM-DD HH24:MI:SS') AS modified_date ");
    sql.append(" FROM board ");
    sql.append(" order BY board_id desc ");

    List<Board> list = template.query(sql.toString(), boardRowMapper());

    return list;
  }

  //상품조회
  @Override
  public Optional<Board> findById(Long id) {
    StringBuffer sql = new StringBuffer();
    sql.append(" SELECT board_id, title, writer, TO_CHAR(created_date, 'YYYY-MM-DD') AS created_date ");
    sql.append(" FROM board ");
    sql.append(" ORDER BY board_id = :id ");
    SqlParameterSource param = new MapSqlParameterSource().addValue("id",id);

    Board board = null;
    try {
      board = template.queryForObject(sql.toString(), param, BeanPropertyRowMapper.newInstance(Board.class));
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }

    return Optional.of(board);
  }

  //게시글삭제(단건)
  @Override
  public int updateById(Long boardId, Board board) {
    StringBuffer sql = new StringBuffer();
    sql.append(" DELETE FROM board ");
    sql.append(" WHERE board_id = id ");
    return 0;
  }

  //게시글삭제(여러건)
  @Override
  public int deleteById(Long id) {
    StringBuffer sql = new StringBuffer();
    sql.append(" DELETE FROM board ");
    sql.append(" WHERE board_id IN = ( :ids ) ");
    return 0;
  }

  //게시글수정
  @Override
  public int deleteByIds(List<Long> ids) {
    StringBuffer sql = new StringBuffer();
    sql.append(" UPDATE board ");
    sql.append(" SET title = '가입인사', content = '안녕하세요, 잘 부탁 드립니다.', modified_date = SYSDATE ");
    sql.append(" WHERE board_id = :boardId ");
    return 0;
  }
}
