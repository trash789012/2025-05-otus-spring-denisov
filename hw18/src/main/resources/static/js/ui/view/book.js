import {parseErrors, parseLastUrlParam} from "../../utils/util.js";
import {createBook, fetchBook, updateBook} from "../../api/bookApi.js";
import {AuthorSelector} from "../components/authorSelector.js";
import {fetchAllAuthors} from "../../api/authorApi.js";
import {fetchAllGenres} from "../../api/genreApi.js";
import {createComment, deleteComment, fetchAllCommentsByBookId} from "../../api/commentApi.js";
import {GenreSelector} from "../components/genreSelector.js";
import {CommentSelector} from "../components/commentSelector.js";

document.addEventListener('DOMContentLoaded', () => {
    const page = new Book(parseLastUrlParam());
    page.init().catch(console.error)
})

export class Book {
    constructor(bookId) {
        this.bookId = bookId;
        this.idInput = document.getElementById('id-input-book');
        this.titleInput = document.getElementById('book-name-input');
        this.commentInput = document.getElementById('add-comment-textarea');
        this.commentForm = document.getElementById('comment-section-id');
        this.authorSelector = new AuthorSelector('book-authors-select', null);
        this.genreSelector = new GenreSelector('book-genres-select');
        this.commentSelector = new CommentSelector('comments-list-card', this.deleteCommentCallback);
        this.saveButton = document.getElementById('book-save');
        if (this.saveButton) {
            this.saveButton.onclick = () => {
                this.saveBook().catch(console.log);
            }
        }
        this.addCommentButton = document.getElementById('add-comment-btn')
        if (this.addCommentButton) {
            this.addCommentButton.onclick = () => {
                this.addCommentCallback(this.idInput.value).catch(console.log);
            }
        }
    }

    init = async () => {
        this.loadBook(this.bookId).catch(console.error);
        if (!this.isEditMode()) {
            this.commentForm.style.display = 'none';
        }
    }

    isEditMode = () => {
        return this.bookId && this.bookId !== 'new';
    }

    loadBook = async (id) => {
        try {
            const [
                authors,
                genres
            ] = await Promise.all([
                fetchAllAuthors(),
                fetchAllGenres()
            ]);

            if (this.isEditMode()) {
                const book = await fetchBook(id);
                const comments = await fetchAllCommentsByBookId(id);

                this.idInput.value = book.id;
                this.titleInput.value = book.title;
                this.authorSelector.authorId = book.authorId;
                this.genreSelector.genreIds = book.genreIds;
                this.commentSelector.render(comments);
            }

            this.authorSelector.render(authors);
            this.genreSelector.render(genres)
        } catch (error) {
            console.error('Error loading book', error);
            alert('Failed to load book');
        }
    }

    saveBook = async () => {

        const bookData = {
            title: this.titleInput.value,
            authorId: this.authorSelector.getValue(),
            genreIds: this.genreSelector.getSelectedValues()
        }

        if (this.idInput.value) {
            bookData.id = this.idInput.value;
        }

        let method = this.idInput.value ? 'PUT' : 'POST';

        let bookNew = !this.idInput.value;

        try {
            let response = null;
            if (method === 'POST') {
                response = await createBook(bookData);
            } else {
                response = await updateBook(bookData.id, bookData);
            }

            if (response.success === false) {
                alert('Filed to save book: \n' + parseErrors(response.errors));
            } else {
                alert('Book saved successfully!');
                if (bookNew) {
                    window.location.replace(`/book/${response.result.id}`);
                } else {
                    await this.loadBook(response.result.id);
                }
            }
        } catch (error) {
            console.error('Error save book:', error);
            alert('Filed to save book');
        }
    }

    deleteCommentCallback = async (bookId, commentId, event) => {
        if (!confirm('Are you sure you want to delete this comment?')) {
            return;
        }
        try {
            await deleteComment(bookId, commentId);

            const commentCard = event.target.closest('.comment-card');
            commentCard.remove();
        } catch (error) {
            console.error('Error deleting comment:', error);
            alert('Filed to delete comment');
        }
    }

    addCommentCallback = async (bookId) => {
        try {
            const commentAdd = {
                id: null,
                text: this.commentInput.value,
                bookId: bookId
            };
            const response = await createComment(bookId, commentAdd);
            if (response.success === false) {
                alert('Filed to add comment: \n' + parseErrors(response.errors));
            } else {
                this.commentSelector.addSingle(response.result);
                this.commentInput.value = '';
            }
        } catch (error) {
            console.error('Error adding comment:', error);
        }
    }
}