import {createGenre, fetchGenre, updateGenre} from "../../api/genreApi.js";
import {parseErrors, parseLastUrlParam} from "../../utils/util.js";

document.addEventListener('DOMContentLoaded', async () => {
    const page = new Genre(parseLastUrlParam());
    page.init().catch(console.error);
})

export class Genre {
    constructor(genreId) {
        this.genreId = genreId;
        this.idInput = document.getElementById('id-input-genre');
        this.fullNameInput = document.getElementById('genre-name-input');
        this.saveButton = document.getElementById('genre-save');
        if (this.saveButton) {
            this.saveButton.onclick = () => {
                this.saveGenre().catch(console.log);
            }
        }
    }

    init = async () => {
        if (this.genreId && this.genreId !== 'new') {
            //edit mode
            this.loadGenre(this.genreId).catch(console.error);
        }
    }

    loadGenre = async (id) => {
        try {
            const genre = await fetchGenre(id);
            this.idInput.value = genre.id;
            this.fullNameInput.value = genre.name;
        } catch (error) {
            console.error('Error loading genre', error);
            alert('Failed to load genre');
        }
    }

    saveGenre = async () => {
        const genreData = {
            name: this.fullNameInput.value
        };

        if (this.idInput.value) {
            genreData.id = this.idInput.value;
        }

        let method;
        method = this.idInput.value ? 'PUT' : 'POST';

        let genreNew = !this.idInput.value;

        try {
            let response = null;
            if (method === 'POST') {
                response = await createGenre(genreData);
            } else {
                response = await updateGenre(genreData.id, genreData);
            }

            if (response.success === false) {
                alert('Filed to save genre: \n' + parseErrors(response.errors));
            } else {
                alert('Genre saved successfully!');
                if (genreNew) {
                    await this.loadGenre(response.result.id);
                } else {
                    this.fullNameInput.value = response.result.name;
                }
            }
        } catch (error) {
            console.error('Error save genre:', error);
            alert('Filed to save genre');
        }
    }
}