
export class AuthorTable {
    constructor(selector, deleteCallback) {
        this.table = document.getElementById(selector);
        this.deleteCallback = deleteCallback;
        if (!this.table) {
            throw new Error(`Element ${selector} not found!`)
        }
    }

    render(authors) {
        this.table.innerHTML = '';

        if (authors == null) {
            return;
        }

        authors.forEach(author => {
            const row = document.createElement('tr');

            // Колонка с именем автора
            const nameCell = document.createElement('td');
            const nameLink = document.createElement('a');
            nameLink.href = `/author/${author.id}`;
            nameLink.textContent = author.fullName;
            nameCell.appendChild(nameLink);

            // Колонка с действиями
            const actionCell = document.createElement('td');
            const deleteButton = document.createElement('button');
            deleteButton.textContent = 'Delete';
            deleteButton.className = 'btn-link';
            deleteButton.onclick = () =>  {
                this.deleteCallback(author.id);
            }
            actionCell.appendChild(deleteButton);

            row.appendChild(nameCell);
            row.appendChild(actionCell);
            this.table.appendChild(row);
        });
    }
}