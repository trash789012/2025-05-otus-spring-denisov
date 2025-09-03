import {AuthorTable} from "../components/authorTable.js";
import {deleteAuthor, fetchAllAuthors} from "../../api/authorApi.js";
import {parseLastUrlParam} from "../../utils/util.js";

document.addEventListener('DOMContentLoaded', () => {
    const page = new Authors(parseLastUrlParam());
    page.init().catch(console.error)
})

export class Authors {
    constructor() {
        this.table = new AuthorTable('authors-table-body', this.deleteAuthor);
    }

    init = async () => {
        this.loadAuthors().catch(console.error);
    }

    loadAuthors = async () => {
        try {
            const authors = await fetchAllAuthors();
            this.table.render(authors);
        } catch (error) {
            console.error('Error loading authors', error);
            alert('Failed to load authors')
        }
    }

    deleteAuthor = async (id) => {
        if (!confirm('Are you sure you want to delete this author?')) {
            return;
        }
        try {
            await deleteAuthor(id);
            await this.loadAuthors();
        } catch (error) {
            console.error('Error deleting author:', error);
            alert('Filed to delete author');
        }
    }
}