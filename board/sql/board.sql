
SELECT * FROM board;
DROP TABLE board;
DROP SEQUENCE board_seq;

-- 게시판 테이블 생성
CREATE TABLE board (
    board_id NUMBER(20) PRIMARY KEY,                  --게시글 아이디
    title VARCHAR2(200),                              --게시글 제목
    content CLOB,                                     --게시글 내용
    writer VARCHAR2(50),                              --게시글 작성자
    created_date TIMESTAMP DEFAULT SYSTIMESTAMP,      --게시글 작성날짜
    modified_date TIMESTAMP DEFAULT SYSTIMESTAMP     --게시글 수정날짜
);
-- 시퀀스 생성
CREATE SEQUENCE board_seq;


-- 데이터 생성
INSERT INTO board (board_id, title, content, writer)
VALUES (board_seq.NEXTVAL, '싯다르타 1 -헤르만 헤세', '지식은 전해줄 수 있지만, 지혜는 전해줄 수 없어.', '밀물');
INSERT INTO board (board_id, title, content, writer)
VALUES (board_seq.NEXTVAL, '싯다르타 2 -헤르만 헤세', '끊임없이 흐르는 강물처럼, 인생도 계속해서 흘러가며 변화한다는 것을 깨닫게 되는 것입니다.', '썰물');
INSERT INTO board (board_id, title, content, writer)
VALUES (board_seq.NEXTVAL, '문장의 무늬들 1 -전영관', '사랑이란 상대와의 끝없는 시차에 당황하는 과정이다.', '단어의 연금술사');
INSERT INTO board (board_id, title, content, writer)
VALUES (board_seq.NEXTVAL, '문장의 무늬들 2 -전영관', '일치와 어긋남이 반복되지만 우리의 삶은 자동차 깜박이보다 심각하게 느리고 길다.', '글자 마법사');
INSERT INTO board (board_id, title, content, writer)
VALUES (board_seq.NEXTVAL, '문장의 무늬들 3 -전영관', '그러나 일치는 순간일 뿐 어느새 어긋나 정반대로 점멸하게 된다.', '이야기꾼');
INSERT INTO board (board_id, title, content, writer)
VALUES (board_seq.NEXTVAL, '삶을 견디는 기쁨 -헤르만 헤세', '우리는 하루하루 살면서 벌어지는 수많은 사소한 일들과 그로 인해 얻은 작은 기쁨들을 하나하나 꿰어 우리의 삶을 엮어 나간다.', '내면의 기쁨');
INSERT INTO board (board_id, title, content, writer)
VALUES (board_seq.NEXTVAL, '공지사항: 게시판 이용 안내', '이 게시판은 테스트용으로 작성되었습니다.', '관리자');
INSERT INTO board (board_id, title, content, writer)
VALUES (board_seq.NEXTVAL, '트렌드코리아 2025 1 -김난도 외', '비지니스 영역에서 시간과 비용의 최적화는 반드시 달성해야 하는 과제지만, 개인적 차원에서는 잠시 멈추고 사색할 수 있는 여유의 시간도 필요하다.' , '디지털 트렌드');
INSERT INTO board (board_id, title, content, writer)
VALUES (board_seq.NEXTVAL, '트렌드코리아 2025 2 -김난도 외', '성찰의 순간이 곧 인간만이 가질 수 있는 판단력을 확보하는 생산의 시간이기 때문이다.' , '이노베이션');
COMMIT;


--게시글 작성
 INSERT INTO board(board_id, title, content, writer)
       VALUES (board_seq.NEXTVAL, '가', '나', '다라');

--게시글 목록
  SELECT board_id, title, writer, created_date, created_date
    FROM board
ORDER BY board_id DESC;

--게시글 조회
 SELECT board_id, title, content, writer, modified_date
   FROM board
  WHERE board_id = 1;

--게시글 수정
UPDATE board
   SET title = '안녕',
       content = '안녕',
       writer = '안녕',
       modified_date = CURRENT_TIMESTAMP
WHERE board_id = 5;

--게시글 삭제
DELETE FROM board
 WHERE board_id = 5;

ROLLBACK;

