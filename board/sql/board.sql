
SELECT * FROM code;
SELECT * FROM member;

--테이블 삭제
drop table member;
drop table uploadfile;
drop table code;
drop table product;

--시퀀스삭제
drop sequence member_member_id_seq;
drop sequence code_code_id_seq;
drop sequence uploadfile_uploadfile_id_seq;
drop sequence product_product_id_seq;
-------
--코드
-------
create table code(
    code_id     varchar2(11),       --코드
    decode      varchar2(30),       --코드명
    discript    clob,               --코드설명
    pcode_id    varchar2(11),       --상위코드
    useyn       char(1) default 'Y',            --사용여부 (사용:'Y',미사용:'N')
    cdate       timestamp default systimestamp,         --생성일시
    udate       timestamp default systimestamp          --수정일시
);
--기본키
alter table code add Constraint code_code_id_pk primary key (code_id);

--외래키
alter table code add constraint bbs_pcode_id_fk
    foreign key(pcode_id) references code(code_id);

--제약조건
alter table code modify decode constraint code_decode_nn not null;
alter table code modify useyn constraint code_useyn_nn not null;
alter table code add constraint code_useyn_ck check(useyn in ('Y','N'));

--시퀀스
create sequence code_code_id_seq;

--샘플데이터 of code
--insert into code (code_id,decode,pcode_id,useyn) values ('B01','게시판',null,'Y');
--insert into code (code_id,decode,pcode_id,useyn) values ('B0101','Spring','B01','Y');
--insert into code (code_id,decode,pcode_id,useyn) values ('B0102','Datbase','B01','Y');
--insert into code (code_id,decode,pcode_id,useyn) values ('B0103','Q_A','B01','Y');
insert into code (code_id,decode,pcode_id,useyn) values ('M01','회원구분',NULL,'Y');
insert into code (code_id,decode,pcode_id,useyn) values ('M0101','일반','M01','Y');
insert into code (code_id,decode,pcode_id,useyn) values ('M0102','우수','M01','Y');
insert into code (code_id,decode,pcode_id,useyn) values ('M01A1','관리자1','M01','Y');
insert into code (code_id,decode,pcode_id,useyn) values ('M01A2','관리자2','M01','Y');

insert into code (code_id,decode,pcode_id,useyn) values ('A02','지역',null,'Y');
insert into code (code_id,decode,pcode_id,useyn) values ('A0201','서울','A02','Y');
insert into code (code_id,decode,pcode_id,useyn) values ('A0202','부산','A02','Y');
insert into code (code_id,decode,pcode_id,useyn) values ('A0203','대구','A02','Y');
insert into code (code_id,decode,pcode_id,useyn) values ('A0204','울산','A02','Y');

insert into code (code_id,decode,pcode_id,useyn) values ('H01','취미',null,'Y');
insert into code (code_id,decode,pcode_id,useyn) values ('H0101','등산','H01','Y');
insert into code (code_id,decode,pcode_id,useyn) values ('H0102','수영','H01','Y');
insert into code (code_id,decode,pcode_id,useyn) values ('H0103','골프','H01','Y');
insert into code (code_id,decode,pcode_id,useyn) values ('H0104','독서','H01','Y');

insert into code (code_id,decode,pcode_id,useyn) values ('F01','첨부파일',null,'Y');
insert into code (code_id,decode,pcode_id,useyn) values ('F0101','회원','F01','Y');
insert into code (code_id,decode,pcode_id,useyn) values ('F010101','사진','F0101','Y');
insert into code (code_id,decode,pcode_id,useyn) values ('F0102','게시판','F01','Y');
insert into code (code_id,decode,pcode_id,useyn) values ('F010201','첨부파일','F0102','Y');
insert into code (code_id,decode,pcode_id,useyn) values ('F0103','상품관리','F01','Y');
insert into code (code_id,decode,pcode_id,useyn) values ('F010301','설명서','F0103','Y');
insert into code (code_id,decode,pcode_id,useyn) values ('F010302','이미지','F0103','Y');

commit;

-------
--회원
-------
create table member (
    member_id   number(10),     --내부 관리 아이디
    email       varchar2(50),   --로긴 아이디
    passwd      varchar2(12),   --로긴 비밀번호
    tel         varchar2(13),   --연락처 ex)010-1234-5678
    nickname    varchar2(30),   --별칭
    gender      varchar2(6),    --성별
    hobby       varchar2(300),  --취미
    region      varchar2(11),   --지역
    gubun       varchar2(11)   default 'M0101', --회원구분 (일반,우수,관리자..)
    pic         blob,            --사진
    cdate       timestamp default systimestamp,         --생성일시
    udate       timestamp default systimestamp          --수정일시
);
--기본키생성
alter table member add Constraint member_member_id_pk primary key (member_id);

--외래키
alter table member add constraint member_region_fk
    foreign key(region) references code(code_id);
alter table member add constraint member_gubun_fk
    foreign key(gubun) references code(code_id);

--제약조건
alter table member modify email constraint member_email_uk unique;
alter table member modify email constraint member_email_nn not null;
alter table member add constraint member_gender_ck check (gender in ('남자','여자'));

--시퀀스
create sequence member_member_id_seq;

--샘플데이터 of member
insert into member (member_id,email,passwd,tel,nickname,gender,hobby,region,gubun)
    values(member_member_id_seq.nextval, 'test1@kh.com', '1234', '010-1111-1111','테스터1','남자','골프,독서','A0201', 'M0101');
insert into member (member_id,email,passwd,tel,nickname,gender,hobby,region,gubun)
    values(member_member_id_seq.nextval, 'test2@kh.com', '1234', '010-1111-1112','테스터2','여자','골프,수영','A0202', 'M0102');
insert into member (member_id,email,passwd,tel,nickname,gender,hobby,region,gubun)
    values(member_member_id_seq.nextval, 'admin1@kh.com', '1234','010-1111-1113','관리자1', '남자','등산,독서','A0203','M01A1');
insert into member (member_id,email,passwd,tel,nickname,gender,hobby,region,gubun)
    values(member_member_id_seq.nextval, 'admin2@kh.com', '1234','010-1111-1114','관리자2', '여자','골프,독서','A0204','M01A2');
select * from member;
commit;
-------------------------------------------------------------------------------------------------------------------------
SELECT * FROM board;
DROP TABLE board;
DROP SEQUENCE board_seq;

-- 게시판 테이블 생성
CREATE TABLE board (
    board_id NUMBER(20) PRIMARY KEY,                  --게시글 번호
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

