import { ajax } from '/js/common.js';

let commentInput, submitButton, commentsList, deleteDialog, confirmDeleteButton, cancelDeleteButton;
let boardId = null;
let comments = [];
let deleteCommentId = null;
let currentUser = null;

// 현재 로그인 사용자 정보 가져오기
const getCurrentUser = async () => {
  try {
    const result = await ajax.get('/api/comments/current-user');

    if (result.success) {
      currentUser = result.data.username;
      return true;
    } else {
      currentUser = null;
      return false;
    }
  } catch (error) {
    currentUser = null;
    return false;
  }
};

// 댓글 목록 가져오기
const getComments = async () => {
  try {
    const result = await ajax.get(`/api/comments/${boardId}`);

    if (result.success) {
      comments = result.data || [];
      renderComments();
    } else {
      comments = [];
      renderComments();
    }
  } catch (error) {
    comments = [];
    renderComments();
  }
};

// 댓글 작성
const addComment = async () => {
  try {
    // 로그인 상태 확인
    if (!currentUser) {
      const loginSuccess = await getCurrentUser();
      if (!loginSuccess) {
        alert('로그인이 필요합니다.');
        return;
      }
    }

    const commentText = commentInput.value.trim();

    if (!commentText) {
      alert('댓글 내용을 입력해주세요.');
      commentInput.focus();
      return;
    }

    // 백엔드 API에 맞는 형태로 데이터 전송
    const requestData = {
      content: commentText,
      writer: currentUser
    };

    const response = await ajax.post(`/api/comments/${boardId}`, requestData);

    if (response.success) {
      commentInput.value = '';
      await getComments();
    } else {
      alert(response.message || '댓글 작성에 실패했습니다.');
    }

  } catch (error) {
    // HTTP 상태 코드별 처리
    if (error.status === 401) {
      alert('로그인이 필요합니다.');
      currentUser = null;
    } else if (error.status === 403) {
      alert('댓글 작성 권한이 없습니다.');
    } else {
      alert('댓글 작성에 실패했습니다.');
    }
  }
};

// 댓글 삭제 확인
function showDeleteDialog(id) {
  deleteCommentId = id;
  deleteDialog.showModal();
}

// 댓글 삭제
const deleteComment = async () => {
  try {
    const response = await ajax.delete(`/api/comments/${deleteCommentId}`);

    deleteDialog.close();

    if (response.success) {
      await getComments();
    } else {
      alert(response.message || '댓글 삭제에 실패했습니다.');
    }

  } catch (error) {
    deleteDialog.close();

    if (error.status === 401) {
      alert('로그인이 필요합니다.');
      currentUser = null;
    } else if (error.status === 403) {
      alert('작성자만 삭제할 수 있습니다.');
    } else if (error.status === 404) {
      alert('댓글을 찾을 수 없습니다.');
    } else {
      alert('댓글 삭제에 실패했습니다.');
    }
  }
};

// 댓글 삭제 취소
const cancelDelete = () => {
  deleteCommentId = null;
  deleteDialog.close();
};

// 댓글 수정
const editComment = async id => {
  try {
    const comment = comments.find(c => c.commentId === id);

    if (!comment) {
      alert('댓글을 찾을 수 없습니다.');
      return;
    }

    // 작성자 권한 확인
    if (comment.writer !== currentUser) {
      alert('작성자만 수정할 수 있습니다.');
      return;
    }

    const newText = prompt('댓글 수정:', comment.content);

    if (newText && newText.trim() !== comment.content) {
      const requestData = {
        content: newText.trim()
      };

      const response = await ajax.patch(`/api/comments/${id}`, requestData);

      if (response.success) {
        await getComments();
      } else {
        alert(response.message || '댓글 수정에 실패했습니다.');
      }
    }
  } catch (error) {
    if (error.status === 401) {
      alert('로그인이 필요합니다.');
      currentUser = null;
    } else if (error.status === 403) {
      alert('작성자만 수정할 수 있습니다.');
    } else if (error.status === 404) {
      alert('댓글을 찾을 수 없습니다.');
    } else {
      alert('댓글 수정에 실패했습니다.');
    }
  }
};

// 댓글 렌더링
function renderComments() {
  if (!commentsList) {
    return;
  }

  commentsList.innerHTML = '';

  if (comments.length === 0) {
    commentsList.innerHTML = '<p class="no-comments">댓글이 없습니다.</p>';
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
    const isMyComment = currentUser && comment.writer === currentUser;

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
        <button type="button" onclick="editComment(${comment.commentId})">수정</button>
        <button type="button" onclick="showDeleteDialog(${comment.commentId})">삭제</button>
      </div>
      ` : ''}
    `;
    commentsList.appendChild(commentDiv);
  });
}

// 댓글 섹션 생성
function createCommentsSection() {
  // 기존 섹션이 있다면 제거
  const existingSection = document.getElementById('comments-section');
  if (existingSection) {
    existingSection.remove();
  }

  const section = document.createElement('div');
  section.id = 'comments-section';
  section.innerHTML = `
    <h3>댓글</h3>
    <div class="comment-form">
      <textarea id="comment-input" placeholder="댓글을 입력하세요" rows="3"></textarea>
      <button type="button" id="submit-button">등록</button>
    </div>
    <div id="comments-list"></div>

    <!-- 삭제 확인 다이얼로그 -->
    <dialog id="delete-dialog">
      <div class="dialog-content">
        <p>정말로 이 댓글을 삭제하시겠습니까?</p>
        <div class="dialog-buttons">
          <button type="button" id="confirm-delete">삭제</button>
          <button type="button" id="cancel-delete">취소</button>
        </div>
      </div>
    </dialog>
  `;

  document.body.appendChild(section);

  // DOM 요소 참조
  commentInput = document.getElementById("comment-input");
  submitButton = document.getElementById("submit-button");
  commentsList = document.getElementById("comments-list");
  deleteDialog = document.getElementById("delete-dialog");
  confirmDeleteButton = document.getElementById("confirm-delete");
  cancelDeleteButton = document.getElementById("cancel-delete");

  // DOM 요소 존재 확인
  const elements = {
    commentInput,
    submitButton,
    commentsList,
    deleteDialog,
    confirmDeleteButton,
    cancelDeleteButton
  };

  for (const [name, element] of Object.entries(elements)) {
    if (!element) {
      return false;
    }
  }

  // 이벤트 리스너 추가
  submitButton.addEventListener("click", addComment);
  confirmDeleteButton.addEventListener("click", deleteComment);
  cancelDeleteButton.addEventListener("click", cancelDelete);

  // Enter 키로 댓글 작성 (Shift+Enter는 줄바꿈)
  commentInput.addEventListener("keydown", (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      addComment();
    }
  });

  return true;
}

// 댓글 섹션 숨기기 (board.js에서 호출)
const hideCommentsSection = () => {
  const section = document.getElementById('comments-section');
  if (section) {
    section.remove();
  }
  // 상태 초기화
  boardId = null;
  comments = [];
  deleteCommentId = null;
};

// 초기화
const init = async id => {
  if (!id) {
    return false;
  }

  boardId = id;

  // 현재 사용자 정보 가져오기
  await getCurrentUser();

  if (createCommentsSection()) {
    getComments();
    return true;
  }

  return false;
};

// 전역 함수 등록
window.showDeleteDialog = showDeleteDialog;
window.editComment = editComment;

const commentManager = { init, hideCommentsSection };
export default commentManager;