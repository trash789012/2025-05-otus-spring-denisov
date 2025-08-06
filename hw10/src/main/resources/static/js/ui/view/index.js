import {BookTable} from "../components/bookTable.js";
import {deleteBook, fetchAllBooks} from "../../api/bookApi.js";

document.addEventListener('DOMContentLoaded', () => {
    const page = new Books();
    page.init().catch(console.error)
})

export class Books {
    constructor() {
        this.table = new BookTable('books-table-body', this.deleteBook);
    }

    init = async () => {
        this.loadBooks().catch(console.error);
    }

    loadBooks = async () => {
        try {
            const books = await fetchAllBooks();
            this.table.render(books);
        } catch (error) {
            console.error('Error loading genres', error);
            alert('Failed to load genres')
        }
    }

    deleteBook = async (id) => {
        try {
            if (!confirm('Are you sure you want to delete this book?')) {
                return;
            }
            await deleteBook(id);
            await this.loadBooks();
        } catch (error) {
            console.error('Error deleting book:', error);
            alert('Filed to delete book');
        }
    }
}