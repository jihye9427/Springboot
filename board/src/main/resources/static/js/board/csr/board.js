import { ajax,  PaginationUI} from '/js/common.js';

let currentPage = 1; // 현재 보고 있는 페이지
let initialPage = 1; // 게시글 등록 후 이동할 페이지

const recordsPerPage = 10;        // 한 페이지당 게시글수
const pagesPerPage = 10;          // 한 페이지당 페이지수

//게시글 등록
const addBoard = async board => {
  try {
    const url = '/api/Boards';
    const result = await ajax.post(url, board);
    if (result.header.rtcd === 'S00') {
      console.log(result.body);
      frm.reset();
      initialPage = 1; // 생성 후 1페이지로 이동
      getBoards(initialPage, recordsPerPage); // 첫 페이지의 기본 레코드로 호출
      configPagination();
    } else if(result.header.rtcd.substr(0,1) == 'E'){
        for(let key in result.header.details){
            console.log(`필드명:${key}, 오류:${result.header.details[key]}`);
        }
    } else {
      alert(result.header.rtmsg);
    }
  } catch (err) {
    console.error(err.message);
  }
};

//게시글 조회
const getBoard = async pid => {
  try {
    const url = `/api/Boards/${pid}`;
    const result = await ajax.get(url);
    console.log(result);
    if (result.header.rtcd === 'S00') {
      console.log(result.body);
      // BoardId2.value = result.body.BoardId;
      BoardId2.setAttribute('value', result.body.BoardId);
      title2.setAttribute('value', result.body.title);
      content2.setAttribute('value', result.body.content);
       writer2.setAttribute('value', result.body. writer);

      BoardId2.value = result.body.BoardId;
      title2.value = result.body.title;
      content2.value =  result.body.content;
      writer2.value = result.body.writer;

    } else if(result.header.rtcd.substr(0,1) == 'E'){
        for(let key in result.header.details){
            console.log(`필드명:${key}, 오류:${result.header.details[key]}`);
        }
    } else {
      alert(result.header.rtmsg);
    }
  } catch (err) {
    console.error(err);
  }
};

//게시글 삭제
const delBoard = async (pid, frm) => {
  try {
    const url = `/api/Boards/${pid}`;
    const result = await ajax.delete(url);
    console.log(result);
    if (result.header.rtcd === 'S00') {
      console.log(result.body);
      const $inputs = frm.querySelectorAll('input');
      [...$inputs].forEach(ele => (ele.value = '')); //폼필드 초기화
      getBoards(currentPage, recordsPerPage); // 현재 페이지의 기본 레코드로 호출
    } else if(result.header.rtcd.substr(0,1) == 'E'){
        for(let key in result.header.details){
            console.log(`필드명:${key}, 오류:${result.header.details[key]}`);
        }
    } else {
      alert(result.header.rtmsg);
    }
  } catch (err) {
    console.error(err);
  }
};

//게시글 수정
const modifyBoard = async (pid, board) => {
  try {
    const url = `/api/Boards/${pid}`;
    const result = await ajax.patch(url, board);
    if (result.header.rtcd === 'S00') {
      console.log(result.body);
      getBoards(currentPage, recordsPerPage); // 현재 페이지의 기본 레코드로 호출
    } else if(result.header.rtcd.substr(0,1) == 'E'){
        for(let key in result.header.details){
            console.log(`필드명:${key}, 오류:${result.header.details[key]}`);
        }
    } else {
      alert(result.header.rtmsg);
    }
  } catch (err) {
    console.error(err.message);
  }
};

//게시글 목록
const getBoards = async (reqPage, reqRec) => {

  try {
    const url = `/api/Boards/paging?pageNo=${reqPage}&numOfRows=${reqRec}`;
    const result = await ajax.get(url);

    if (result.header.rtcd === 'S00') {
      currentPage = reqPage; // 현재 페이지 업데이트
      displayBoardList(result.body);

    } else {
      alert(result.header.rtmsg);
    }
  } catch (err) {
    console.error(err);
  }
};

//게시글 등록 화면
function displayForm() {
  //게시글 등록
  const $addFormWrap = document.createElement('div');
  $addFormWrap.innerHTML = `
    <form id="frm">
      <div>
          <label for="title">제목</label>
          <input type="text" id="title" name="title"/>
          <span class="field-error client" id="errTitle"></span>
      </div>
      <div>
          <label for="content">내용</label>
          <input type="text" id="content" name="content"/>
          <span class="field-error client" id="errContent"></span>
      </div>
      <div>
          <label for="writer">작성자</label>
          <input type="text" id="writer" name="writer"/>
          <span class="field-error client" id="errWriter"></span>
      </div>
      <div>
          <button id="btnAdd" type="submit">등록</button>
      </div>
    </form>
  `;

  document.body.insertAdjacentElement('afterbegin', $addFormWrap);
  const $frm = $addFormWrap.querySelector('#frm');
  $frm.addEventListener('submit', e => {
    e.preventDefault(); // 기본동작 중지

    //유효성 체크
    if($frm.title.value.trim().length === 0) {
      errTitle.textContent = '제목 입력은 필수 입니다';
      $frm.title.focus();
      return;
    }
    if($frm.content.value.trim().length === 0) {
      errContent.textContent = '내용 입력은 필수 입니다';
      $frm.content.focus();
      return;
    }
    if($frm.writer.value.trim().length === 0) {
      errWriter.textContent = '작성자 입력은 필수 입니다';
      $frm.writer.focus();
      return;
    }

    const formData = new FormData(e.target); //폼데이터가져오기
    const Board = {};
    [...formData.keys()].forEach(
      ele => (Board[ele] = formData.get(ele)),
    );

    addBoard(Board);

  });
}

//게시글 조회 화면
function displayReadForm() {
  //상태 : 조회 mode-read, 편집 mode-edit
  const changeEditMode = frm => {
    frm.classList.toggle('mode-edit', true);
    [...frm.querySelectorAll('input')]
      .filter(input => input.name !== 'BoardId')
      .forEach(input => input.removeAttribute('readonly'));

    const $btns = frm.querySelector('.btns');
    $btns.innerHTML = `
      <button id="btnSave" type="button">저장</button>
      <button id="btnCancel" type="button">취소</button>
    `;

    const $btnSave = $btns.querySelector('#btnSave');
    const $btnCancel = $btns.querySelector('#btnCancel');

    //저장
    $btnSave.addEventListener('click', e => {
      const formData = new FormData(frm); //폼데이터가져오기
      const Board = {};

      [...formData.keys()].forEach(
        ele => (Board[ele] = formData.get(ele)),
      );

      modifyBoard(Board.BoardId, Board); //수정
      getBoard(Board.BoardId); //조회
      changeReadMode(frm); //읽기모드
    });

    //취소
    $btnCancel.addEventListener('click', e => {
      frm.reset(); //초기화
      changeReadMode(frm);
    });
  };

  const changeReadMode = frm => {
    frm.classList.toggle('mode-read', true);
    [...frm.querySelectorAll('input')]
      .filter(input => input.name !== 'BoardId')
      .forEach(input => input.setAttribute('readonly', ''));

    const $btns = frm.querySelector('.btns');
    $btns.innerHTML = `
      <button id="btnEdit" type="button">수정</button>
      <button id="btnDelete" type="button">삭제</button>
    `;

    const $btnDelete = $btns.querySelector('#btnDelete');
    const $btnEdit = $btns.querySelector('#btnEdit');

    //수정
    $btnEdit.addEventListener('click', e => {
      changeEditMode(frm);
    });

    //삭제
    $btnDelete.addEventListener('click', e => {
      const pid = frm.BoardId.value;
      if (!pid) {
        alert('게시글 조회 후 삭제바랍니다.');
        return;
      }

      if (!confirm('삭제하시겠습니까?')) return;
      delBoard(pid, frm);
    });
  };

  const $readFormWrap = document.createElement('div');
  $readFormWrap.innerHTML = `
    <form id="frm2">

      <div>
          <label for="BoardId2">게시글아이디</label>
          <input type="text" id="BoardId2" name="BoardId" readonly/>
      </div>
      <div>
          <label for="title">제목</label>
          <input type="text" id="title2" name="title"/>
      </div>
      <div>
          <label for="content">내용</label>
          <input type="text" id="content2" name="content"/>
      </div>
      <div>
          <label for="writer">작성자</label>
          <input type="text" id="writer2" name="writer"/>
      </div>
      </div>
      <div class='btns'></div>

    </form>
  `;

  document.body.insertAdjacentElement('afterbegin', $readFormWrap);
  const $frm2 = $readFormWrap.querySelector('#frm2');
  changeReadMode($frm2);
}

//게시글 목록 화면
function displayBoardList(Boards) {

  const makeTr = Boards => {
    const $tr = Boards
      .map(
        Board =>
          `<tr data-pid=${Board.BoardId}>
            <td>${Board.BoardId}</td>
            <td>${Board.title}</td></tr>`,
      )
      .join('');
    return $tr;
  };

  $list.innerHTML = `
    <table>
      <caption> 게 시 글 목 록 </caption>
      <thead>
        <tr>
          <th>게시글번호</th>
          <th>게시글명</th>
        </tr>
      </thead>
      <tbody>
        ${makeTr(Boards)}
      </tbody>
    </table>`;

  const $Boards = $list.querySelectorAll('table tbody tr');

  // Array.from($Boards)
  [...$Boards].forEach(Board =>
    Board.addEventListener('click', e => {
      const pid = e.currentTarget.dataset.pid;
      getBoard(pid);
    }),
  );
}

displayReadForm(); //조회
displayForm();//등록
//getBoards();//목록

const $list = document.createElement('div');
$list.setAttribute('id','list')
document.body.appendChild($list);

const divEle = document.createElement('div');
divEle.setAttribute('id','reply_pagenation');
document.body.appendChild(divEle);

async function configPagination(){
  const url = '/api/Boards/totCnt';
  try {
    const result = await ajax.get(url);

    const totalRecords = result.body; // 전체 레코드수

    const handlePageChange = (reqPage)=>{
      return getBoards(reqPage,recordsPerPage);
    };

    // Pagination UI 초기화
    var pagination = new PaginationUI('reply_pagenation', handlePageChange);

    pagination.setTotalRecords(totalRecords);       //총건수
    pagination.setRecordsPerPage(recordsPerPage);   //한페이지당 레코드수
    pagination.setPagesPerPage(pagesPerPage);       //한페이지당 페이지수

    // 첫페이지 가져오기
    pagination.handleFirstClick();

  }catch(err){
    console.error(err);
  }
}
configPagination();