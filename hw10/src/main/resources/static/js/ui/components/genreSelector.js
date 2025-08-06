export class GenreSelector {
    constructor(selector, genreIds) {
        this.genreSelect = document.getElementById(selector);
        if (!this.genreSelect) {
            throw new Error(`Element ${selector} not found!`)
        }
        this.genreIds = genreIds;
    }

    render(genres) {
        this.genreSelect.innerHTML = '';

        genres.forEach(genre => {
            const option = document.createElement('option');
            option.value = genre.id;
            option.textContent = genre.name;
            option.selected = this.genreIds.includes(genre.id);

            this.genreSelect.appendChild(option);
        });
    }
}