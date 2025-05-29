import { ajax } from './common.js'; // common.js에서 ajax 가져오기

let commentInput, submitButton, commentsList, deleteDialog, confirmDeleteButton, cancelDeleteButton;
let boardId = 1; // 테스트용 게시글 ID (실제로는 동적으로 설정)
let comments = [];
let deleteCommentId = null;
let currentUser = null;

// 현재 로그인 사용자 정보 가져오기
const getCurrentUser = async () => {
  try {
    const result = await ajax.get('/api/comments/current-user');

    if (result.header && result.header.rtcd === 'S00') {
      currentUser = result.body.username;
      console.log('현재 로그인 사용자:', currentUser);
      return true;
    } else {
      currentUser = null;
      console.log('로그인되지 않은 사용자');
      return false;
    }
  } catch (error) {
    console.error('사용자 정보 조회 실패:', error);
    currentUser = null;
    return false;
  }
};

// 댓글 목록 가져오기
const getComments = async () => {
  try {
    const result = await ajax.get(`/api/comments/${boardId}`);

    if (result.header && result.header.rtcd === 'S00') {
      comments = result.body || [];
      console.log('댓글 목록 조회 성공:', comments);
      renderComments();
    } else {
      console.log('댓글 목록 조회 실패:', result?.header?.rtmsg);
      comments = [];
      renderComments();
    }
  } catch (error) {
    console.error('댓글 목록 조회 중 오류:', error);
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

    const requestData = {
      content: commentText,
      writer: currentUser
    };

    const response = await ajax.post(`/api/comments/${boardId}`, requestData);

    if (response.header && response.header.rtcd === 'S00') {
      commentInput.value = '';
      console.log('댓글 작성 성공');
      await getComments();
    } else {
      alert(response.header?.rtmsg || '댓글 작성에 실패했습니다.');
    }

  } catch (error) {
    console.error('댓글 작성 중 오류:', error);
    if (error.message.includes('401')) {
      alert('로그인이 필요합니다.');
      currentUser = null;
    } else if (error.message.includes('403')) {
      alert('댓글 작성 권한이 없습니다.');
    } else {
      alert('댓글 작성에 실패했습니다.');
    }
  }
};

// 댓글 삭제 확인 다이얼로그 표시
const showDeleteDialog = (id) => {
  deleteCommentId = id;
  deleteDialog.showModal();
};

// 댓글 삭제
const deleteComment = async () => {
  try {
    const response = await ajax.delete(`/api/comments/${deleteCommentId}`);

    deleteDialog.close();

    if (response.header && response.header.rtcd === 'S00') {
      console.log('댓글 삭제 성공');
      await getComments();
    } else {
      alert(response.header?.rtmsg || '댓글 삭제에 실패했습니다.');
    }

  } catch (error) {
    deleteDialog.close();
    console.error('댓글 삭제 중 오류:', error);

    if (error.message.includes('401')) {
      alert('로그인이 필요합니다.');
      currentUser = null;
    } else if (error.message.includes('403')) {
      alert('작성자만 삭제할 수 있습니다.');
    } else if (error.message.includes('404')) {
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
const editComment = async (id) => {
  try {
    const comment = comments.find(c => c.commentId === id);

    if (!comment) {
      alert('댓글을 찾을 수 없습니다.');
      return;
    }

    // 작성자 권한 확인 (클라이언트 사이드 추가 검증)
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

      if (response.header && response.header.rtcd === 'S00') {
        console.log('댓글 수정 성공');
        await getComments();
      } else {
        alert(response.header?.rtmsg || '댓글 수정에 실패했습니다.');
      }
    }
  } catch (error) {
    console.error('댓글 수정 중 오류:', error);
    if (error.message.includes('401')) {
      alert('로그인이 필요합니다.');
      currentUser = null;
    } else if (error.message.includes('403')) {
      alert('작성자만 수정할 수 있습니다.');
    } else if (error.message.includes('404')) {
      alert('댓글을 찾을 수 없습니다.');
    } else {
      alert('댓글 수정에 실패했습니다.');
    }
  }
};

// 댓글 렌더링 (작성자 권한 기반)
function renderComments() {
  if (!commentsList) {
    console.error('댓글 목록 요소를 찾을 수 없습니다.');
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

    // 현재 사용자가 작성한 댓글인지 확인 (핵심 권한 체크)
    const isMyComment = currentUser && comment.writer === currentUser;

    commentDiv.innerHTML = `
      <div class="comment-content">
        <div class="comment-text">${escapedContent}</div>
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

  console.log(`댓글 ${comments.length}개 렌더링 완료. 현재 사용자: ${currentUser}`);
}

// DOM이 로드된 후 초기화
document.addEventListener('DOMContentLoaded', async () => {
  // DOM 요소 참조
  commentInput = document.getElementById("comment-input");
  submitButton = document.getElementById("submit-button");
  commentsList = document.getElementById("comments-list");
  deleteDialog = document.getElementById("delete-dialog");
  confirmDeleteButton = document.getElementById("confirm-delete");
  cancelDeleteButton = document.getElementById("cancel-delete");

  // DOM 요소 존재 확인
  const elements = { commentInput, submitButton, commentsList, deleteDialog, confirmDeleteButton, cancelDeleteButton };
  for (const [name, element] of Object.entries(elements)) {
    if (!element) {
      console.error(`${name} 요소를 찾을 수 없습니다.`);
      return;
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

  // 전역 함수 등록 (HTML onclick에서 사용)
  window.showDeleteDialog = showDeleteDialog;
  window.editComment = editComment;

  // 초기 데이터 로드
  console.log('댓글 시스템 초기화 시작...');
  await getCurrentUser();
  await getComments();
  console.log('댓글 시스템 초기화 완료');
});