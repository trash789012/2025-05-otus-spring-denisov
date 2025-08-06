export class CommentSelector {
    constructor(selector) {
        this.commentSelect = document.getElementById(selector);
        if (!this.commentSelect) {
            throw new Error(`Element ${selector} not found!`)
        }
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
        formElement.action = `/books/${bookId}/comments/${commentId}/delete`;

        const deleteButton = document.createElement('button');
        deleteButton.type = 'submit';
        deleteButton.className = 'delete-comment-btn';
        deleteButton.title = 'Delete comment';
        deleteButton.textContent = '×';

        formElement.appendChild(deleteButton);
        commentContent.append(textElement, formElement);
        commentCard.appendChild(commentContent);

        return commentCard;
    }
}