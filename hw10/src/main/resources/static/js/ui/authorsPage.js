import {AuthorTable} from "./components/table.js";
import {deleteAuthor, fetchAllAuthors} from "../api/authorApi.js";

export class AuthorsPage {
    constructor() {
        this.table = new AuthorTable('authors-table-body', this.deleteAuthor);
    }

    loadAuthors = async() => {
        try {
            const authors = await fetchAllAuthors();
            this.table.render(authors);
        } catch (error) {
            console.error('Error loading authors', error);
            alert('Failed to load authors')
        }
    }

    deleteAuthor = async (id) => {
        try {
            await deleteAuthor(id);
            await this.loadAuthors();
        } catch (error) {
            console.error('Error deleting author:', error);
            alert('Filed to delete author');
        }
    }
}