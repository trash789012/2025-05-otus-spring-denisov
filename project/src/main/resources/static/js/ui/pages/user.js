import {UserTabs} from "../components/userTabs.js";
import {fetchUserById} from "../../api/userApi.js";
import {parseLastUrlParam} from "../../utils/util.js";

document.addEventListener('DOMContentLoaded', function () {
    const userId = parseLastUrlParam('userId');
    const userPage = new User(userId);
    userPage.init().catch(console.error);
});

export class User {
    constructor(userId = 0) {
        this.userId = userId;
        this.view = new UserTabs();
    }

    init = async () => {
        this.loadUserInfo(this.userId).catch(console.error);
    }

    loadUserInfo = async (userId) => {
        try {
            const user = await fetchUserById(userId);
            this.view.renderMainInfo(user);
            this.view.renderRolesSelector(user.roles);
        } catch (e) {
            console.error(e);
        }
    }
}