package com.kdt.board.domain.entity;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Comments {
  private Long commentId;                //댓글 고유 아이디
  private Long boardId;                 //(댓글이 속한) 게시글 아이디
  private Long parentId;               //대댓글의 아이디
  private String writer;              //댓글 작성자
  private String content;            //댓글 내용
  private Timestamp createdAt;     //게시글 작성날짜
  private Timestamp updatedAt;    //게시글 수정날짜
}
