import {parseLastUrlParam} from "../../utils/util.js";
import {fetchBook} from "../../api/bookApi.js";
import {AuthorSelector} from "../components/authorSelector.js";
import {fetchAllAuthors} from "../../api/authorApi.js";
import {fetchAllGenres} from "../../api/genreApi.js";
import {deleteComment, fetchAllCommentsByBookId} from "../../api/commentApi.js";
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
        this.authorSelector = new AuthorSelector('book-authors-select', null);
        this.genreSelector = new GenreSelector('book-genres-select');
        this.commentSelector = new CommentSelector('comments-list-card', this.deleteCommentCallback);
        this.saveButton = document.getElementById('book-save');
        if (this.saveButton) {
            this.saveButton.onclick = () => {
                this.saveBook().catch(console.log);
            }
        }
    }

    init = async () => {
        if (this.bookId && this.bookId !== 'new') {
            //edit mode
            this.loadBook(this.bookId).catch(console.error);
        }
    }

    loadBook = async (id) => {
        try {
            const [
                book,
                authors,
                genres,
                comments
            ] = await Promise.all([
                fetchBook(id),
                fetchAllAuthors(),
                fetchAllGenres(),
                fetchAllCommentsByBookId(id)
            ]);

            this.idInput.value = book.id;
            this.titleInput.value = book.title;

            this.authorSelector.authorId = book.authorId;
            this.authorSelector.render(authors);

            this.genreSelector.genreIds = book.genreIds;
            this.genreSelector.render(genres)

            this.commentSelector.render(comments);
        } catch (error) {
            console.error('Error loading book', error);
            alert('Failed to load book');
        }
    }

    saveBook = async () => {
        // const authorData = {
        //     fullName: this.fullNameInput.value
        // };
        //
        // if (this.idInput.value) {
        //     authorData.id = this.idInput.value;
        // }
        //
        // let method = this.idInput.value ? 'PUT' : 'POST';
        //
        // let authorNew;
        // authorNew = !this.idInput.value;
        //
        // try {
        //     let response = null;
        //     if (method === 'POST') {
        //         response = createAuthor(authorData);
        //     } else {
        //         response = updateAuthor(authorData.id, authorData);
        //     }
        //
        //     await response.then((updatedAuthor) => {
        //         alert('Author saved successfully!');
        //         if (authorNew) {
        //             this.loadAuthor(updatedAuthor.id);
        //         } else {
        //             this.fullNameInput.value = updatedAuthor.fullName;
        //         }
        //     })
        // } catch (error) {
        //     console.error('Error save author:', error);
        //     alert('Filed to save author');
        // }
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
            console.error('Error deleting author:', error);
            alert('Filed to delete author');
        }
    }
}