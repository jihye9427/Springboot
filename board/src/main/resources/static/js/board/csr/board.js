import { ajax, PaginationUI } from '/js/common.js';

const doBoard = () => {
  // 상수 및 설정
  const CONFIG = {
    recordsPerPage: 10,         // 한 페이지에 보여줄 게시글 수
    pagesPerPage: 10,           // 한 페이지에 보여줄 게시글 페이지 수
    commentRecordsPerPage: 10,  // 한 페이지에 보여줄 댓글 수
    commentPagesPerPage: 5      // 한 페이지에 보여줄 댓글 페이지 수
  };

  // 상태 관리
  const state = {
    currentPage: 1,
    currentBoardId: null,
    currentCommentPage: 1,
    currentUser: null,
    viewMode: 'list' // 'list' 또는 'detail'
  };

  // DOM 요소 참조
  const elements = {
    addBoardForm: null,
    readBoardForm: null,
    BoardId2: null,
    title2: null,
    content2: null,
    writer2: null,
    list: document.createElement('div'),
    pagination: document.createElement('div'),

    // 댓글 관련 요소들
    commentsSection: null,
    commentInput: null,
    commentsList: null,
    commentPagination: document.createElement('div')
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

    // 현재 로그인 사용자 정보 가져오기
    async getCurrentUser() {
      const result = await this.call('get', '/api/comments/current-user');
      if (result && result.header.rtcd === 'S00') {
        state.currentUser = result.body.username;
        return true;
      } else {
        state.currentUser = null;
        return false;
      }
    },

    // 게시글 관련 API
    async addBoard(Board) {
      const url = '/api/boards';
      const result = await this.call('post', url, Board);

      if (result && result.header.rtcd === 'S00') {
        console.log('게시글 등록 성공:', result.body);
        if (elements.addBoardForm) {
          elements.addBoardForm.reset();
          elements.addBoardForm.querySelectorAll('.field-error.client').forEach(span => span.textContent = '');
        }
        // 등록 후 목록으로 이동
        ui.showListView();
        this.getBoards(1);
      } else if (result && result.header.rtcd.substr(0,1) == 'E') {
        if (result.header.details) {
          for(let key in result.header.details){
            console.log(`필드명:${key}, 오류:${result.header.details[key]}`);
            const errSpan = elements.addBoardForm.querySelector(`#err${key.charAt(0).toUpperCase() + key.slice(1)}`);
            if (errSpan) errSpan.textContent = result.header.details[key];
          }
        }
      } else {
        console.log('게시글 등록 실패:', result?.header.rtmsg);
        alert(`게시글 등록 실패: ${result?.header.rtmsg || '알 수 없는 오류'}`);
      }
    },

    async getBoard(pid) {
      const url = `/api/boards/${pid}`;
      const result = await this.call('get', url);

      if (result && result.header.rtcd === 'S00') {
        console.log('게시글 조회 성공:', result.body);
        ui.updateFormFields(result.body);
        ui.showDetailView();

        // 댓글 섹션 초기화
        state.currentBoardId = result.body.BoardId;
        await this.getCurrentUser();
        ui.createCommentsSection();
        await comment.getComments(1);
        await comment.initCommentPagination();

      } else if (result && result.header.rtcd.substr(0,1) == 'E') {
        if (result.header.details) {
          for(let key in result.header.details){
            console.log(`필드명:${key}, 오류:${result.header.details[key]}`);
          }
        }
      } else {
        console.log('게시글 조회 실패:', result?.header.rtmsg);
        alert(`게시글 조회 실패: ${result?.header.rtmsg || '알 수 없는 오류'}`);
      }
    },

    async deleteBoard(pid, formToReset) {
      const url = `/api/boards/${pid}`;
      const result = await this.call('delete', url);

      if (result && result.header.rtcd === 'S00') {
        console.log('게시글 삭제 성공:', result.body);
        if (formToReset) {
          const $inputs = formToReset.querySelectorAll('input, textarea');
          [...$inputs].forEach(ele => (ele.value = ''));
        }

        // 댓글 섹션 숨기기
        ui.hideCommentsSection();
        state.currentBoardId = null;

        // 삭제 후 목록으로 이동
        ui.showListView();
        this.getBoards(state.currentPage);
      } else if (result && result.header.rtcd.substr(0,1) == 'E') {
        if (result.header.details) {
          for(let key in result.header.details){
            console.log(`필드명:${key}, 오류:${result.header.details[key]}`);
          }
        }
      } else {
        console.log('게시글 삭제 실패:', result?.header.rtmsg);
        alert(`게시글 삭제 실패: ${result?.header.rtmsg || '알 수 없는 오류'}`);
      }
    },

    async modifyBoard(pid, Board) {
      const url = `/api/boards/${pid}`;
      const result = await this.call('patch', url, Board);

      if (result && result.header.rtcd === 'S00') {
        console.log('게시글 수정 성공:', result.body);
        // 수정 후 해당 게시글 다시 조회
        this.getBoard(pid);
      } else if (result && result.header.rtcd.substr(0,1) == 'E') {
        if (result.header.details) {
          for(let key in result.header.details){
            console.log(`필드명:${key}, 오류:${result.header.details[key]}`);
          }
        }
      } else {
        console.log('게시글 수정 실패:', result?.header.rtmsg);
        alert(`게시글 수정 실패: ${result?.header.rtmsg || '알 수 없는 오류'}`);
      }
    },

    async getBoards(pageNo) {
      const url = `/api/boards/paging?pageNo=${pageNo}&numOfRows=${CONFIG.recordsPerPage}`;
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
      const url = '/api/boards/totCnt';
      const result = await this.call('get', url);

      if (result && result.body != null) {
        return Number(result.body);
      }
      return 0;
    },

    // 댓글 관련 API (통일된 응답 형식 사용)
    async addComment(commentData) {
      if (!state.currentUser) {
        const loginSuccess = await this.getCurrentUser();
        if (!loginSuccess) {
          alert('로그인이 필요합니다.');
          return false;
        }
      }

      const url = `/api/comments/${state.currentBoardId}`;
      const result = await this.call('post', url, commentData);

      if (result && result.header.rtcd === 'S00') {
        console.log('댓글 등록 성공');
        return true;
      } else if (result && result.header.rtcd.substr(0,1) == 'E') {
        alert(result.header.rtmsg);
        return false;
      } else {
        alert('댓글 작성에 실패했습니다.');
        return false;
      }
    },

    async getComments(pageNo) {
      if (!state.currentBoardId) return [];

      const url = `/api/comments/${state.currentBoardId}/paging?pageNo=${pageNo}&numOfRows=${CONFIG.commentRecordsPerPage}`;
      const result = await this.call('get', url);

      if (result && result.header.rtcd === 'S00') {
        state.currentCommentPage = pageNo;
        return result.body || [];
      } else {
        console.log('댓글 목록 조회 실패:', result?.header.rtmsg);
        return [];
      }
    },

    async getCommentTotalCount() {
      if (!state.currentBoardId) return 0;

      const url = `/api/comments/${state.currentBoardId}/totCnt`;
      const result = await this.call('get', url);

      if (result && result.body != null) {
        return Number(result.body);
      }
      return 0;
    },

    async updateComment(commentId, content) {
      const url = `/api/comments/${commentId}`;
      const result = await this.call('patch', url, { content });

      if (result && result.header.rtcd === 'S00') {
        console.log('댓글 수정 성공');
        return true;
      } else if (result && result.header.rtcd.substr(0,1) == 'E') {
        alert(result.header.rtmsg);
        return false;
      } else {
        alert('댓글 수정에 실패했습니다.');
        return false;
      }
    },

    async deleteComment(commentId) {
      const url = `/api/comments/${commentId}`;
      const result = await this.call('delete', url);

      if (result && result.header.rtcd === 'S00') {
        console.log('댓글 삭제 성공');
        return true;
      } else if (result && result.header.rtcd.substr(0,1) == 'E') {
        alert(result.header.rtmsg);
        return false;
      } else {
        alert('댓글 삭제에 실패했습니다.');
        return false;
      }
    }
  };

  // 댓글 관련 기능
  const comment = {
    async addComment() {
      if (!elements.commentInput || !elements.commentInput.value.trim()) {
        alert('댓글 내용을 입력해주세요.');
        if (elements.commentInput) elements.commentInput.focus();
        return;
      }

      // 로그인 사용자 확인
      if (!state.currentUser) {
        const loginSuccess = await api.getCurrentUser();
        if (!loginSuccess) {
          alert('로그인이 필요합니다.');
          return;
        }
      }

      const commentData = {
        content: elements.commentInput.value.trim(),
        writer: state.currentUser
      };

      const success = await api.addComment(commentData);
      if (success) {
        elements.commentInput.value = '';
        await this.getComments(1);
        await this.initCommentPagination();
      }
    },

    async getComments(pageNo) {
      const comments = await api.getComments(pageNo);
      this.renderComments(comments);
    },

    async editComment(commentId) {
      // 로그인 확인
      if (!state.currentUser) {
        await api.getCurrentUser();
      }

      // 현재 댓글 찾기
      const commentElement = document.querySelector(`#comment-${commentId}`);
      if (!commentElement) {
        alert('댓글을 찾을 수 없습니다.');
        return;
      }

      const contentElement = commentElement.querySelector('.comment-text');
      const currentContent = contentElement.textContent;

      const newContent = prompt('댓글 수정:', currentContent);
      if (newContent && newContent.trim() !== currentContent) {
        const success = await api.updateComment(commentId, newContent.trim());
        if (success) {
          await this.getComments(state.currentCommentPage);
        }
      }
    },

    async deleteComment(commentId) {
      if (confirm('정말로 이 댓글을 삭제하시겠습니까?')) {
        const success = await api.deleteComment(commentId);
        if (success) {
          await this.getComments(state.currentCommentPage);
          await this.initCommentPagination();
        }
      }
    },

    renderComments(comments) {
      if (!elements.commentsList) return;

      elements.commentsList.innerHTML = '';

      if (comments.length === 0) {
        elements.commentsList.innerHTML = '<p class="no-comments">댓글이 없습니다.</p>';
        return;
      }

      comments.forEach(comment => {
        const commentDiv = document.createElement("div");
        commentDiv.id = `comment-${comment.commentId}`;
        commentDiv.className = 'comment';

        // XSS 방지를 위한 텍스트 이스케이프
        const escapedContent = comment.content
          .replace(/&/g, '&amp;')
          .replace(/</g, '&lt;')
          .replace(/>/g, '&gt;')
          .replace(/"/g, '&quot;')
          .replace(/'/g, '&#39;');

        // 현재 사용자가 작성한 댓글인지 확인
        const isMyComment = state.currentUser && comment.writer === state.currentUser;

        commentDiv.innerHTML = `
          <div class="comment-content">
            <span class="comment-text">${escapedContent}</span>
            <div class="comment-meta">
              <span class="comment-writer">작성자: ${comment.writer || '익명'}</span>
              <span class="comment-date">${comment.createDate || ''}</span>
            </div>
          </div>
          ${isMyComment ? `
          <div class="comment-buttons">
            <button type="button" onclick="window.editComment(${comment.commentId})">수정</button>
            <button type="button" onclick="window.deleteComment(${comment.commentId})">삭제</button>
          </div>
          ` : ''}
        `;
        elements.commentsList.appendChild(commentDiv);
      });
    },

    async initCommentPagination() {
      if (!state.currentBoardId) return;

      elements.commentPagination.setAttribute('id', 'comment_pagination');
      elements.commentPagination.innerHTML = '';

      const totalRecords = await api.getCommentTotalCount();

      if (totalRecords > 0) {
        const commentPaginationCallback = (page) => this.getComments(page);

        const pagination = new PaginationUI('comment_pagination', commentPaginationCallback);
        pagination.setTotalRecords(totalRecords);
        pagination.setRecordsPerPage(CONFIG.commentRecordsPerPage);
        pagination.setPagesPerPage(CONFIG.commentPagesPerPage);

        pagination.handleFirstClick();
      }
    }
  };

  // UI 핸들러
  const ui = {
    // 뷰 모드 변경
    showListView() {
      state.viewMode = 'list';
      // 등록 폼 보이기
      if (elements.addBoardForm && elements.addBoardForm.parentElement) {
        elements.addBoardForm.parentElement.style.display = 'block';
      }
      // 상세 폼 숨기기
      if (elements.readBoardForm && elements.readBoardForm.parentElement) {
        elements.readBoardForm.parentElement.style.display = 'none';
      }
      // 목록과 페이지네이션 보이기
      elements.list.style.display = 'block';
      elements.pagination.style.display = 'block';
      // 댓글 섹션 숨기기
      this.hideCommentsSection();
    },

    showDetailView() {
      state.viewMode = 'detail';
      // 등록 폼 숨기기
      if (elements.addBoardForm && elements.addBoardForm.parentElement) {
        elements.addBoardForm.parentElement.style.display = 'none';
      }
      // 상세 폼 보이기
      if (elements.readBoardForm && elements.readBoardForm.parentElement) {
        elements.readBoardForm.parentElement.style.display = 'block';
      }
      // 목록과 페이지네이션 숨기기
      elements.list.style.display = 'none';
      elements.pagination.style.display = 'none';
    },

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
        if (inputElement && inputElement.value.trim().length === 0) {
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
              ${field.name === 'content'
                ? `<textarea id="${field.name}" name="${field.name}" rows="3"></textarea>`
                : `<input type="text" id="${field.name}" name="${field.name}"/>`
              }
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
          <div class="form-header">
            <button id="btnList" type="button">목록</button>
          </div>
          <div>
              <label for="BoardId2">게시글아이디</label>
              <input type="text" id="BoardId2" name="BoardId" readonly />
          </div>
          <div>
              <label for="title2">제목</label>
              <input type="text" id="title2" name="title" />
          </div>
          <div>
              <label for="content2">내용</label>
              <textarea id="content2" name="content" rows="5"></textarea>
          </div>
          <div>
              <label for="writer2">작성자</label>
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

      // 목록 버튼 이벤트 리스너 추가
      const $btnList = elements.readBoardForm.querySelector('#btnList');
      $btnList.addEventListener('click', () => {
        this.showListView();
        api.getBoards(state.currentPage);
      });

      this.changeReadMode(elements.readBoardForm);
      document.body.insertAdjacentElement('afterbegin', $readFormContainer);

      // 초기에는 숨김
      $readFormContainer.style.display = 'none';
    },

    //게시글 조회 모드
    changeReadMode(frm) {
      frm.classList.remove('mode-edit');
      frm.classList.add('mode-read');
      this.toggleInputReadOnly(frm, true);

      const $btns = frm.querySelector('.btns');

      // 현재 사용자와 작성자가 같은지 확인
      const currentWriter = elements.writer2 ? elements.writer2.value : '';
      const isAuthor = state.currentUser && currentWriter === state.currentUser;

      // 작성자만 수정/삭제 버튼 표시
      if (isAuthor) {
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
      } else {
        // 작성자가 아니면 버튼 숨김
        $btns.innerHTML = '';
      }
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
        this.changeReadMode(frm);
      });

      $btnCancel.replaceWith($btnCancel.cloneNode(true));
      $btns.querySelector('#btnCancel').addEventListener('click', () => {
        const boardIdInput = frm.querySelector('#BoardId2');
        if (boardIdInput && boardIdInput.value) {
          api.getBoard(boardIdInput.value);
        }
        this.changeReadMode(frm);
      });
    },

    toggleInputReadOnly(frm, isReadOnly) {
      [...frm.querySelectorAll('input, textarea')].forEach(input => {
        if (input.name !== 'BoardId') {
          input.readOnly = isReadOnly;
        }
      });
    },

    displayBoardList(Boards) {
      elements.list.setAttribute('id', 'list');

      if (!Array.isArray(Boards)) {
        console.error('displayBoardList: Boards 인자는 배열이어야 합니다.', Boards);
        elements.list.innerHTML = '<p class="alert">게시글 데이터를 불러올 수 없습니다.</p>';
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

    // 게시글 페이지네이션
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
    },

    // 댓글 섹션 생성
    createCommentsSection() {
      // 기존 섹션이 있다면 제거
      this.hideCommentsSection();

      const section = document.createElement('div');
      section.id = 'comments-section';
      section.innerHTML = `
        <h3>댓글</h3>
        <div class="comment-form">
          <textarea id="comment-input" placeholder="댓글을 입력하세요" rows="3"></textarea>
          <button type="button" id="submit-comment">등록</button>
        </div>
        <div id="comments-list"></div>
        <div id="comment_pagination"></div>
      `;

      document.body.appendChild(section);

      // DOM 요소 참조
      elements.commentsSection = section;
      elements.commentInput = section.querySelector("#comment-input");
      elements.commentsList = section.querySelector("#comments-list");
      elements.commentPagination = section.querySelector("#comment_pagination");

      const submitButton = section.querySelector("#submit-comment");

      // 이벤트 리스너 추가
      submitButton.addEventListener("click", () => comment.addComment());

      // Enter 키로 댓글 작성 (Shift+Enter는 줄바꿈)
      elements.commentInput.addEventListener("keydown", (e) => {
        if (e.key === 'Enter' && !e.shiftKey) {
          e.preventDefault();
          comment.addComment();
        }
      });
    },

    // 댓글 섹션 숨기기
    hideCommentsSection() {
      const section = document.getElementById('comments-section');
      if (section) {
        section.remove();
      }

      // 댓글 관련 상태 초기화
      elements.commentsSection = null;
      elements.commentInput = null;
      elements.commentsList = null;
      state.currentCommentPage = 1;
    }
  };

  // 전역 함수 등록 (댓글 수정/삭제용)
  window.editComment = (commentId) => comment.editComment(commentId);
  window.deleteComment = (commentId) => comment.deleteComment(commentId);

  // 초기화 및 실행
  const init = async () => {
    // DOM 요소 준비
    document.body.appendChild(elements.list);
    document.body.appendChild(elements.pagination);

    // 폼 생성
    ui.createReadForm();
    ui.createAddForm();

    // 현재 사용자 정보 가져오기
    await api.getCurrentUser();

    // 초기 목록 뷰 표시
    ui.showListView();

    // 페이지네이션 및 데이터 로드
    await ui.initPagination();
  };

  // 공개 API
  return {
    init
  };
};

// 게시글 관리 모듈
const BoardManager = doBoard();

// 애플리케이션 시작
document.addEventListener('DOMContentLoaded', BoardManager.init);