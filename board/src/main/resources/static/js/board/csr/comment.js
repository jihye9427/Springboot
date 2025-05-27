import { ajax } from '/js/board/common.js';

let commentInput, submitButton, commentsList, deleteDialog, confirmDeleteButton, cancelDeleteButton;
let boardId = null;
let comments = [];
let deleteCommentId = null;

// 댓글 목록 가져오기
async function getComments() {
    const result = await ajax.get(`/api/comments/${boardId}`);
    comments = result.data || [];
    renderComments();
}

// 댓글 작성
async function addComment() {
    const commentText = commentInput.value.trim();
    if (commentText) {
        await ajax.post(`/api/comments/${boardId}`, {
            content: commentText,
            writer: '사용자'
        });
        commentInput.value = '';
        getComments();
    }
}

// 댓글 삭제 확인
function showDeleteDialog(id) {
    deleteCommentId = id;
    deleteDialog.showModal();
}

// 댓글 삭제
async function deleteComment() {
    await ajax.delete(`/api/comments/${deleteCommentId}`);
    deleteDialog.close();
    getComments();
}

// 댓글 삭제 취소
function cancelDelete() {
    deleteDialog.close();
}

// 댓글 수정
async function editComment(id) {
    const comment = comments.find(c => c.commentId === id);
    const newText = prompt('댓글 수정:', comment.content);

    if (newText) {
        await ajax.patch(`/api/comments/${id}`, { content: newText });
        getComments();
    }
}

// 댓글 렌더링
function renderComments() {
    commentsList.innerHTML = '';

    comments.forEach(comment => {
        const commentDiv = document.createElement("div");
        commentDiv.id = `comment-${comment.commentId}`;
        commentDiv.className = 'comment';
        commentDiv.innerHTML = `
            <span class="comment-text">${comment.content}</span>
            <span class="comment-buttons">
                <button onclick="showDeleteDialog(${comment.commentId})">삭제</button>
                <button onclick="editComment(${comment.commentId})">수정</button>
            </span>
        `;
        commentsList.appendChild(commentDiv);
    });
}

// 댓글 섹션 생성
function createCommentsSection() {
    const section = document.createElement('div');
    section.innerHTML = `
        <h3>댓글</h3>
        <div>
            <textarea id="comment-input" placeholder="댓글 입력"></textarea>
            <button id="submit-button">등록</button>
        </div>
        <div id="comments-list"></div>

        <!-- 삭제 확인 다이얼로그 -->
        <dialog id="delete-dialog">
            <div>
                <p>정말로 이 댓글을 삭제하시겠습니까?</p>
                <button id="confirm-delete">삭제</button>
                <button id="cancel-delete">취소</button>
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

    // 이벤트 리스너
    submitButton.addEventListener("click", addComment);
    confirmDeleteButton.addEventListener("click", deleteComment);
    cancelDeleteButton.addEventListener("click", cancelDelete);
}

// 초기화
function init(id) {
    boardId = id;
    createCommentsSection();
    getComments();
}

// 전역 함수
window.showDeleteDialog = showDeleteDialog;
window.editComment = editComment;

const commentManager = { init };
export default commentManager;