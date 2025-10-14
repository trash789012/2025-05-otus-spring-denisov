export class AuthorSelector {
    constructor(selector, authorId) {
        this.authorSelect = document.getElementById(selector);
        if (!this.authorSelect) {
            throw new Error(`Element ${selector} not found!`)
        }
        this.authorId = authorId;
    }

    render(authors) {
        this.authorSelect.innerHTML = '';

     authors.forEach(author => {
         const option = document.createElement('option');
         option.value = author.id;
         option.textContent = author.fullName;
         option.selected = this.authorId === author.id;

         this.authorSelect.appendChild(option);
 });
    }

    getValue() {
        return this.authorSelect.value;
    }
}