import {UserCards} from "./user/userCards.js";

export class AdminTabs {
    constructor(props = {}) {
        this.userCards = new UserCards();

        this.userCards.addEventListener('delete', (userId) => {
            props.onUserDelete(userId);
        });
    }

    renderUsers(users = []) {
        this.userCards.render(users);
    }

}