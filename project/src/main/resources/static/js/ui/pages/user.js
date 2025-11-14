import {UserTabs} from "../components/userTabs.js";
import {createUser, deleteUser, fetchAllRoles, fetchUserById, updateUserAndRoles} from "../../api/userApi.js";
import {parseLastUrlParam} from "../../utils/util.js";

document.addEventListener('DOMContentLoaded', function () {
    const userId = parseLastUrlParam('userId');
    const userPage = new User(userId);
    userPage.init().catch(console.error);
});

export class User {
    constructor(userId) {
        this.userId = userId;
        this.view = new UserTabs({
            saveBtnEvent: this.onSaveUserBtnClick,
            deleteBtnEvent: this.onDeleteUserBtnClick
        });
        this.setEditMode(this.userId);
    }

    init = async () => {
        this.loadUserInfo(this.userId).catch(console.error);
    }

    loadUserInfo = async (userId) => {
        try {
            let roles = [];
            let user = {};
            if (this.editMode) {
                [user, roles] = await Promise.all([
                    fetchUserById(userId),
                    fetchAllRoles()
                ]);
            } else {
                roles = await fetchAllRoles();
            }

            this.view.setAllRoles(roles);
            if (this.editMode) {
                this.view.renderMainInfo(user);
            }
            this.view.renderRolesSelector(user?.roles);
            this.view.renderUserGroups(user?.groups);
        } catch (e) {
            console.error(e);
        }
    }

    onSaveUserBtnClick = async () => {
        try {
            const userDto = this.view.prepareForApi();
            let result = {};
            if (this.editMode) {
                result = await updateUserAndRoles(userDto.id, userDto);
            } else {
                result = await createUser(userDto);
            }
            if (result.success) {
                this.userId = result.result?.id;
                if (!this.editMode) {
                    window.history.go(-1);
                    return;
                }
                this.setEditMode(this.userId);
                this.init().catch(console.error);
            }
        } catch (e) {
            console.error(e);
        }
    }

    onDeleteUserBtnClick = async () => {
        try {
            await deleteUser(this.userId);
            window.history.go(-1);
        } catch (e) {
            console.error(e);
        }
    }

    setEditMode(userId) {
        if (userId == null || userId === 'new') {
            this.userId = 0;
            this.editMode = false;
        } else {
            this.userId = userId;
            this.editMode = true;
        }
        if (this.editMode) {
            this.view.disableLogin();
            this.view.requiredPassword(false);
        } else {
            this.view.enableLogin();
            this.view.requiredPassword();
        }
    }
}