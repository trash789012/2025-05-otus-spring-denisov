import {createAuthor, fetchAuthor, updateAuthor} from "../../api/authorApi.js";
import {parseLastUrlParam} from "../../utils/util.js";

document.addEventListener('DOMContentLoaded', () => {
    const page = new Author(parseLastUrlParam());
    page.init().catch(console.error)
})

export class Author {
    constructor(authorId) {
        this.authorId = authorId;
        this.idInput = document.getElementById('id-input');
        this.fullNameInput = document.getElementById('author-name-input');
        this.saveButton = document.getElementById('author-save');
        if (this.saveButton) {
            this.saveButton.onclick = () => {
                this.saveAuthor().catch(console.log);
            }
        }
    }

    init = async () => {
        if (this.authorId && this.authorId !== 'new') {
            //edit mode
            this.loadAuthor(this.authorId).catch(console.error);
        }
    }

    loadAuthor = async (id) => {
        try {
            const author = await fetchAuthor(id);
            this.idInput.value = author.id;
            this.fullNameInput.value = author.fullName;
        } catch (error) {
            console.error('Error loading author', error);
            alert('Failed to load author');
        }
    }

    saveAuthor = async () => {
        const authorData = {
            fullName: this.fullNameInput.value
        };

        if (this.idInput.value) {
            authorData.id = this.idInput.value;
        }

        let method = this.idInput.value ? 'PUT' : 'POST';

        let authorNew;
        authorNew = !this.idInput.value;

        try {
            let response = null;
            if (method === 'POST') {
                response = createAuthor(authorData);
            } else {
                response = updateAuthor(authorData.id, authorData);
            }

            await response.then((updatedAuthor) => {
                alert('Author saved successfully!');
                if (authorNew) {
                    this.loadAuthor(updatedAuthor.id);
                } else {
                    this.fullNameInput.value = updatedAuthor.fullName;
                }
            })
        } catch (error) {
            console.error('Error save author:', error);
            alert('Filed to save author');
        }
    }
}