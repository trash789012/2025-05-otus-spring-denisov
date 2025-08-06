
export class GenreTable {
    constructor(selector, deleteCallback) {
        this.table = document.getElementById(selector);
        this.deleteCallback = deleteCallback;
        if (!this.table) {
            throw new Error(`Element ${selector} not found!`)
        }
    }

    render(genres) {
        this.table.innerHTML = '';

        if (genres == null) {
            return;
        }

        genres.forEach(genre => {
            const row = document.createElement('tr');

            // Колонка с именем жанра
            const nameCell = document.createElement('td');
            const nameLink = document.createElement('a');
            nameLink.href = `/author/${genre.id}`;
            nameLink.textContent = genre.name;
            nameCell.appendChild(nameLink);

            // Колонка с действиями
            const actionCell = document.createElement('td');
            const deleteButton = document.createElement('button');
            deleteButton.textContent = 'Delete';
            deleteButton.className = 'btn-link';
            deleteButton.onclick = () =>  {
                this.deleteCallback(genre.id);
            }
            actionCell.appendChild(deleteButton);

            row.appendChild(nameCell);
            row.appendChild(actionCell);
            this.table.appendChild(row);
        });
    }
}