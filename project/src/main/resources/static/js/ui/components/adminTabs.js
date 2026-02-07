import {UserCards} from "./cards/userCards.js";
import {GroupCards} from "./cards/groupCards.js";

export class AdminTabs {
    constructor(props = {}) {
        this.userCards = new UserCards();

        this.userCards.addEventListener('delete', (userId) => {
            props.onUserDelete(userId);
        });

        this.groupCards   = new GroupCards();

        this.groupCards.addEventListener('delete', (groupId) => {
            props.onGroupDelete(groupId);
        });

        this.btnCreate = document.getElementById("btnCreate");
        this.btnCreateGroup = document.getElementById("btnCreateGroup");
        this.btnCreate.onclick = () => {
            props.onCreate();
        }
        this.btnCreateGroup.onclick = () => {
            props.onCreate();
        }
    }

    removeUserCard(userId) {
        this.userCards.removeCard(userId);
    }

    removeGroupCard(groupId) {
        this.groupCards.removeCard(groupId);
    }

    renderUsersCards(users = []) {
        this.userCards.render(users);
    }

    renderGroups(groups = []) {
        this.groupCards.render(groups);
    }

}