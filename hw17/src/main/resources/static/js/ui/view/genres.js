import {GenreTable} from "../components/genreTable.js";
import {deleteGenre, fetchAllGenres} from "../../api/genreApi.js";

document.addEventListener('DOMContentLoaded', () => {
    const page = new Genres();
    page.init().catch(console.error)
})

export class Genres {
    constructor() {
        this.table = new GenreTable('genres-table-body', this.deleteGenre);
    }

    init = async () => {
        this.loadGenres().catch(console.error);
    }

    loadGenres = async () => {
        try {
            const genres = await fetchAllGenres();
            this.table.render(genres);
        } catch (error) {
            console.error('Error loading genres', error);
            alert('Failed to load genres')
        }
    }

    deleteGenre = async (id) => {
        try {
            if (!confirm('Are you sure you want to delete this genre?')) {
                return;
            }
            await deleteGenre(id);
            await this.loadGenres();
        } catch (error) {
            console.error('Error deleting genre:', error);
            alert('Filed to delete genre');
        }
    }
}