import {AuthorsPage} from "./ui/authorsPage.js";

document.addEventListener('DOMContentLoaded', () => {
   const page = new AuthorsPage();
   page.loadAuthors().catch(console.error);
});