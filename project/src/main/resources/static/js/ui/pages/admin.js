import {AdminTabs} from "../components/adminTabs.js";
import {deleteUser, fetchAllUsersWithRoles} from "../../api/userApi.js";
import {deleteGroup, fetchAllGroups} from "../../api/groupApi.js";

document.addEventListener('DOMContentLoaded', function () {
    const adminPage = new Admin();
    adminPage.init().catch(console.error);
});

export class Admin {
    constructor() {
        this.adminView = new AdminTabs({
            onUserDelete: this.onUserDelete,
            onGroupDelete: this.onGroupDelete,
            onCreate: this.onCreate
        });

        this.view = 'users';
    }

    init = async () => {
        this.loadUsers().catch(console.error);

        const navButtons = document.querySelectorAll('.admin-nav-btn');
        const contents = document.querySelectorAll('.entity-content');

        const that = this;
        navButtons.forEach(button => {
            button.addEventListener('click', function(e) {
                e.preventDefault();

                const hash = window.location.hash.replace('#', '');

                navButtons.forEach(btn => btn.classList.remove('active'));
                this.classList.add('active');

                contents.forEach(content => content.classList.remove('active'));

                const target = this.getAttribute('data-target');
                that.view = target;
                switch (target) {
                    case 'users': {
                        that.loadUsers().catch(console.error);
                    } break;
                    case 'groups': {
                        that.loadGroups().catch(console.error);
                    } break;
                }

                const targetId = this.getAttribute('data-target') + '-content';
                document.getElementById(targetId).classList.add('active');
            });
        });
    }

    loadUsers = async () => {
        try {
            const allUsers = await fetchAllUsersWithRoles();
            this.adminView.renderUsersCards(allUsers);
        } catch (e) {
            console.error(e);
        }
    }

    loadGroups = async () => {
        try {
            const allGroups = await fetchAllGroups();
            this.adminView.renderGroups(allGroups);
        } catch (e) {
            console.error(e);
        }
    }

    onUserDelete = async (userId) => {
        try {
            await deleteUser(userId);
            this.adminView.removeUserCard(userId);
        } catch (e) {
            console.error(e);
        }
    }

    onCreate = async () => {
        switch (this.view) {
            case 'users': {
                window.location.href = '/admin/user/new';
            } break;
            case 'groups': {
                window.location.href = '/admin/group/new';
            } break;
        }

    }

    onGroupDelete = async (groupId) => {
        try {
            await deleteGroup(groupId);
            this.adminView.removeGroupCard(groupId);
        } catch (e) {
            console.error(e);
        }
    }
}