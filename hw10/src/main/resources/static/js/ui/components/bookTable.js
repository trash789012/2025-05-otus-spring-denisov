
export class BookTable {
    constructor(selector, deleteCallback) {
        this.table = document.getElementById(selector);
        this.deleteCallback = deleteCallback;
        if (!this.table) {
            throw new Error(`Element ${selector} not found!`)
        }
    }

    render(books) {
        this.table.innerHTML = '';

        if (books == null) {
            return;
        }

        books.forEach(book => {
            const row = document.createElement('tr');

            // Колонка с именем книги
            const nameCell = document.createElement('td');
            const nameLink = document.createElement('a');
            nameLink.href = `/book/${book.id}`;
            nameLink.textContent = book.title;
            nameCell.appendChild(nameLink);

            // Колонка с автором книги
            const authorCell = document.createElement('td');
            authorCell.textContent = book.author.fullName;

            // Колонка с действиями
            const actionCell = document.createElement('td');
            const deleteButton = document.createElement('button');
            deleteButton.textContent = 'Delete';
            deleteButton.className = 'btn-link';
            deleteButton.onclick = () =>  {
                this.deleteCallback(book.id);
            }
            actionCell.appendChild(deleteButton);

            row.appendChild(nameCell);
            row.appendChild(authorCell);
            row.appendChild(actionCell);
            this.table.appendChild(row);
        });
    }
}