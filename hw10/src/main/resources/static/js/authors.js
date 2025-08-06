import {AuthorPage} from "./ui/authorPage.js";

document.addEventListener('DOMContentLoaded', () => {
   const page = new AuthorPage();
   page.loadAuthors().catch(console.error);
});