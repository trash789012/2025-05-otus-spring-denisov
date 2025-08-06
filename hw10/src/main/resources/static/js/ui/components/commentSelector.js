export class CommentSelector {
    constructor(selector, deleteCallback) {
        this.commentSelect = document.getElementById(selector);
        if (!this.commentSelect) {
            throw new Error(`Element ${selector} not found!`)
        }

        this.deleteCallback = deleteCallback;
    }

    render(comments) {
        this.commentSelect.innerHTML = '';

        comments.forEach(comment => {
            let card = this.createCommentCard(comment.text, comment.id, comment.bookId);
            this.commentSelect.appendChild(card);
        });
    }

    createCommentCard(commentText, commentId, bookId) {
        const commentCard = document.createElement('div');
        commentCard.className = 'comment-card';

        const commentContent = document.createElement('div');
        commentContent.className = 'comment-content';

        const textElement = document.createElement('p');
        textElement.className = 'comment-text';
        textElement.textContent = commentText;

        const formElement = document.createElement('form');
        formElement.method = 'post';

        const deleteButton = document.createElement('button');
        deleteButton.type = 'button';
        deleteButton.className = 'delete-comment-btn';
        deleteButton.title = 'Delete comment';
        deleteButton.textContent = '×';
        deleteButton.onclick = (event) => {
            this.deleteCallback(bookId, commentId, event);
        }

        formElement.appendChild(deleteButton);
        commentContent.append(textElement, formElement);
        commentCard.appendChild(commentContent);

        return commentCard;
    }
}