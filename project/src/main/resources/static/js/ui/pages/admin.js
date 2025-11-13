import {AdminTabs} from "../components/adminTabs.js";
import {deleteUser, fetchAllUsersWithRoles} from "../../api/userApi.js";

document.addEventListener('DOMContentLoaded', function () {
    const adminPage = new Admin();
    adminPage.init().catch(console.error);
});

export class Admin {
    constructor() {
        this.adminView = new AdminTabs({
            onUserDelete: this.onUserDelete
        });
    }

    init = async () => {
        this.loadUsers().catch(console.error);
    }

    loadUsers = async () => {
        try {
            const allUsers = await fetchAllUsersWithRoles();
            this.adminView.renderUsers(allUsers);
        } catch (e) {
            console.error(e);
        }
    }

    onUserDelete = async (userId) => {
        try {
            await deleteUser(userId);
        } catch (e) {
            console.error(e);
        }
    }
}