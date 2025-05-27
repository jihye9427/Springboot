import { ajax, PaginationUI } from '/js/common.js';

const doBoard = ()=>{
  // 상수 및 설정
  const CONFIG = {
    recordsPerPage: 10,     //한 페이지에 보여줄 행 수
    pagesPerPage: 10        //한 페이지에 보여줄 페이지 수
  };

  // 상태 관리
  const state = {
    currentPage: 1
  };

  // DOM 요소 참조
  const elements = {
    addBoardForm: null,           // 게시글추가
    readBoardForm: null,          // 게시글조회

    BoardId2: null,               // 게시글아이디
    title2: null,                   // 게시글명
    content2: null,                // 게시글수량
    writer2: null,                   // 게시글가격

    list: document.createElement('div'),            // 게시글목록
    pagination: document.createElement('div')       // 게시글페이지
  };

  // 필드 구성
  const formFieldsConfig = {
    add: [
      { label: '제목', name: 'title' },
      { label: '내용', name: 'content' },
      { label: '작성자', name: 'writer' }
    ]
  };

  // API 호출 핸들러
  const api = {
    async call(method, url, data) {
      try {
        const result = await ajax[method](url, data);
        return result;
      } catch (err) {
        console.error(`API 호출 오류 (${method.toUpperCase()} ${url}):`, err);
        alert(`데이터 처리 중 오류가 발생했습니다: ${err.message}`);
        return null;
      }
    },

    async addBoard(Board) {
      const url = '/api/Boards';
      const result = await this.call('post', url, Board);

      if (result && result.header.rtcd === 'S00') {
        console.log('게시글 등록 성공:', result.body);
        if (elements.addBoardForm) {
          elements.addBoardForm.reset();
          elements.addBoardForm.querySelectorAll('.field-error.client').forEach(span => span.textContent = '');
        }
        this.getBoards(1);
      } else {
        console.log('게시글 등록 실패:', result?.header.rtmsg);
        alert(`게시글 등록 실패: ${result?.header.rtmsg || '알 수 없는 오류'}`);
      }
    },

    async getBoard(pid) {
      const url = `/api/Boards/${pid}`;
      const result = await this.call('get', url);

      if (result && result.header.rtcd === 'S00') {
        console.log('게시글 조회 성공:', result.body);
        ui.updateFormFields(result.body);
      } else {
        console.log('게시글 조회 실패:', result?.header.rtmsg);
        alert(`게시글 조회 실패: ${result?.header.rtmsg || '알 수 없는 오류'}`);
      }
    },

    async deleteBoard(pid, formToReset) {
      const url = `/api/Boards/${pid}`;
      const result = await this.call('delete', url);

      if (result && result.header.rtcd === 'S00') {
        console.log('게시글 삭제 성공:', result.body);
        if (formToReset) {
          formToReset.reset();
        }
        this.getBoards(state.currentPage);
      } else {
        console.log('게시글 삭제 실패:', result?.header.rtmsg);
        alert(`게시글 삭제 실패: ${result?.header.rtmsg || '알 수 없는 오류'}`);
      }
    },

    async modifyBoard(pid, Board) {
      const url = `/api/Boards/${pid}`;
      const result = await this.call('patch', url, Board);

      if (result && result.header.rtcd === 'S00') {
        console.log('게시글 수정 성공:', result.body);
        this.getBoards(state.currentPage);
      } else {
        console.log('게시글 수정 실패:', result?.header.rtmsg);
        alert(`게시글 수정 실패: ${result?.header.rtmsg || '알 수 없는 오류'}`);
      }
    },

    async getBoards(pageNo) {
      const url = `/api/Boards/paging?pageNo=${pageNo}&numOfRows=${CONFIG.recordsPerPage}`;
      const result = await this.call('get', url);

      if (result && result.header.rtcd === 'S00') {
        console.log('게시글 목록 조회 성공 (페이지:', pageNo, ')');
        state.currentPage = pageNo;
        ui.displayBoardList(result.body || []);
      } else {
        console.log('게시글 목록 조회 실패:', result?.header.rtmsg);
        ui.displayBoardList([]);
      }
    },

    async getTotalCount() {
      const url = '/api/Boards/totCnt';
      const result = await this.call('get', url);

      if (result && result.body != null) {
        return Number(result.body);
      }
      return 0;
    }
  };

  // UI 핸들러
  const ui = {
    updateFormFields(Board) {
      if (elements.BoardId2 && elements.title2 && elements.content2 && elements.writer2) {
        elements.BoardId2.value = Board.BoardId || '';
        elements.title2.value = Board.title || '';
        elements.content2.value = Board.content || '';
        elements.writer2.value = Board.writer || '';
      } else {
        console.error('게시글 조회/수정 폼 필드가 초기화되지 않았습니다.');
      }
    },

    validateForm(formElement, fieldConfigs) {
      let isValid = true;
      // 기존 에러 메시지 초기화
      fieldConfigs.forEach(field => {
        const errSpan = formElement.querySelector(`#err${field.name.charAt(0).toUpperCase() + field.name.slice(1)}`);
        if (errSpan) errSpan.textContent = '';
      });

      for (const field of fieldConfigs) {
        const inputElement = formElement[field.name];
        if (inputElement.value.trim().length === 0) {
          const errSpan = formElement.querySelector(`#err${field.name.charAt(0).toUpperCase() + field.name.slice(1)}`);
          if (errSpan) errSpan.textContent = `${field.label}은(는) 필수 항목입니다.`;
          if (isValid) {
            inputElement.focus();
          }
          isValid = false;
        }
      }
      return isValid;
    },

    //게시글 등록 양식
    createAddForm() {
      const $formContainer = document.createElement('div');
      $formContainer.innerHTML = `
        <form id="frmAddBoard">
          ${formFieldsConfig.add.map(field => `
            <div>
              <label for="${field.name}">${field.label}</label>
              <input type="text" id="${field.name}" name="${field.name}"/>
              <span class="field-error client" id="err${field.name.charAt(0).toUpperCase() + field.name.slice(1)}"></span>
            </div>
          `).join('')}
          <div>
              <button type="submit">등록</button>
          </div>
        </form>
      `;

      elements.addBoardForm = $formContainer.querySelector('#frmAddBoard');

      elements.addBoardForm.addEventListener('submit', e => {
        e.preventDefault();
        if (!this.validateForm(elements.addBoardForm, formFieldsConfig.add)) return;

        const formData = new FormData(e.target);
        const Board = Object.fromEntries(formData.entries());
        api.addBoard(Board);
      });

      document.body.insertAdjacentElement('afterbegin', $formContainer);
    },

    //게시글 조회 양식
    createReadForm() {
      const $readFormContainer = document.createElement('div');
      $readFormContainer.innerHTML = `
        <form id="frm2">
          <div>
              <label for="BoardId2">게시글아이디</label>
              <input type="text" id="BoardId2" name="BoardId" readonly />
          </div>
          <div>
              <label for="title2">게시글명</label>
              <input type="text" id="title2" name="title" />
          </div>
          <div>
              <label for="content2">수량</label>
              <input type="text" id="content2" name="content" />
          </div>
          <div>
              <label for="writer2">가격</label>
              <input type="text" id="writer2" name="writer" />
          </div>
          <div class='btns'></div>
        </form>
      `;

      elements.readBoardForm = $readFormContainer.querySelector('#frm2');

      // 폼 필드 요소들 참조 할당
      if (elements.readBoardForm) {
        elements.BoardId2 = elements.readBoardForm.querySelector('#BoardId2');
        elements.title2 = elements.readBoardForm.querySelector('#title2');
        elements.content2 = elements.readBoardForm.querySelector('#content2');
        elements.writer2 = elements.readBoardForm.querySelector('#writer2');
      }

      this.changeReadMode(elements.readBoardForm);
      document.body.insertAdjacentElement('afterbegin', $readFormContainer);
    },

    //게시글 조회 모드
    changeReadMode(frm) {
      frm.classList.remove('mode-edit');
      frm.classList.add('mode-read');
      this.toggleInputReadOnly(frm, true);

      const $btns = frm.querySelector('.btns');
      $btns.innerHTML = `
        <button id="btnEdit" type="button">수정</button>
        <button id="btnDelete" type="button">삭제</button>
      `;

      const $btnEdit = $btns.querySelector('#btnEdit');
      const $btnDelete = $btns.querySelector('#btnDelete');

      $btnEdit.replaceWith($btnEdit.cloneNode(true));
      $btns.querySelector('#btnEdit').addEventListener('click', () => this.changeEditMode(frm));

      $btnDelete.replaceWith($btnDelete.cloneNode(true));
      $btns.querySelector('#btnDelete').addEventListener('click', () => {
        const pid = frm.querySelector('#BoardId2').value;
        if (!pid) {
          alert('게시글 조회 후 삭제바랍니다.');
          return;
        }
        if (confirm('삭제하시겠습니까?')) api.deleteBoard(pid, frm);
      });
    },
    //게시글 수정 모드
    changeEditMode(frm) {
      frm.classList.remove('mode-read');
      frm.classList.add('mode-edit');
      this.toggleInputReadOnly(frm, false);

      const $btns = frm.querySelector('.btns');
      $btns.innerHTML = `
        <button id="btnSave" type="button">저장</button>
        <button id="btnCancel" type="button">취소</button>
      `;

      const $btnSave = $btns.querySelector('#btnSave');
      const $btnCancel = $btns.querySelector('#btnCancel');

      $btnSave.replaceWith($btnSave.cloneNode(true));
      $btns.querySelector('#btnSave').addEventListener('click', () => {
        const formData = new FormData(frm);
        const Board = Object.fromEntries(formData.entries());
        if (!Board.BoardId) {
          alert('게시글 ID가 없습니다. 수정할 수 없습니다.');
          return;
        }
        api.modifyBoard(Board.BoardId, Board);
        api.getBoard(Board.BoardId);
        this.changeReadMode(frm);
      });

      $btnCancel.replaceWith($btnCancel.cloneNode(true));
      $btns.querySelector('#btnCancel').addEventListener('click', () => {
        const boardIdInput = frm.querySelector('#BoardId2');
        if (boardIdInput) {
          api.getBoard(boardIdInput.value);
        }
        this.changeReadMode(frm);
      };
    },

    toggleInputReadOnly(frm, isReadOnly) {
      [...frm.querySelectorAll('input')].forEach(input => {
        if (input.name !== 'BoardId') {
          input.readOnly = isReadOnly;
        }
      });
    },

    displayBoardList(Boards) {
      elements.list.setAttribute('id', 'list');

      if (!Array.isArray(Boards)) {
        console.error('displayBoardList: Boards 인자는 배열이어야 합니다.', Boards);
        elements.list.innerHTML = '<tr><td colspan="2">게시글 데이터를 불러올 수 없습니다.</td></tr>';
        return;
      }

      const makeTr = Boards.map(Board => `
        <tr data-pid="${Board.BoardId}">
          <td>${Board.BoardId}</td>
          <td>${Board.title}</td>
        </tr>
      `).join('');

      elements.list.innerHTML = `
        <table>
          <caption>게시글 목록</caption>
          <thead>
            <tr>
              <th>게시글번호</th>
              <th>게시글명</th>
            </tr>
          </thead>
          <tbody>
            ${makeTr || '<tr><td colspan="2">게시글이 없습니다.</td></tr>'}
          </tbody>
        </table>`;

      const $BoardRows = elements.list.querySelectorAll('tbody tr[data-pid]');
      $BoardRows.forEach(BoardRow =>
        BoardRow.addEventListener('click', e => {
          const pid = e.currentTarget.dataset.pid;
          api.getBoard(pid);
        })
      );
    },

    // 페이지네이션
    async initPagination() {
      elements.pagination.setAttribute('id', 'reply_pagenation');

      const totalRecords = await api.getTotalCount();

      if (totalRecords > 0) {
        const paginationCallback = (page) => api.getBoards(page);

        const pagination = new PaginationUI('reply_pagenation', paginationCallback);
        pagination.setTotalRecords(totalRecords);
        pagination.setRecordsPerPage(CONFIG.recordsPerPage);
        pagination.setPagesPerPage(CONFIG.pagesPerPage);

        pagination.handleFirstClick();
      } else {
        this.displayBoardList([]);
        console.log('등록된 게시글이 없습니다.');
      }
    }
  };

  // 초기화 및 실행
  const init = async () => {
    // DOM 요소 준비
    document.body.appendChild(elements.list);
    document.body.appendChild(elements.pagination);

    // 폼 생성
    ui.createReadForm();
    ui.createAddForm();

    // 페이지네이션 및 데이터 로드
    await ui.initPagination();
  };

  // 공개 API
  return {
    init
  };
}
// 게시글 관리 모듈
const BoardManager = doBoard();

// 애플리케이션 시작
document.addEventListener('DOMContentLoaded', BoardManager.init);