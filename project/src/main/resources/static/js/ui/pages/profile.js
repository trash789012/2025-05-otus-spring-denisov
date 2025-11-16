import {getUserData, updateUser} from "../../api/userApi.js";
import {ProfileTabs} from "../components/profileTabs.js";
import {fetchGroupMembers} from "../../api/groupApi.js";
import {Notification} from "../../utils/notifications.js";

document.addEventListener('DOMContentLoaded', function () {
    const profilePage = new Profile();
    profilePage.init().catch(console.error);
});

export class Profile {
    constructor() {
        this.notifications = new Notification();
        this.view = new ProfileTabs({
            userName: 'userName',
            userLastName: 'userLastName',
            userDescription: 'userDescription',
            saveProfileBtn: 'saveProfileBtn',
            saveBtnEvent: this.onSaveProfileBtnClick,
            viewGroupMembersEvt: this.onViewGroupMembers
        });
    }

    init = async () => {
        try {
            const user = await getUserData();
            this.loadProfile(user).catch(console.error);
            this.loadGroups(user).catch(console.error);
        } catch (e) {
            console.error(e);
            this.notifications.error(e.message);
        }
    }

    loadProfile = async (user) => {
        try {
            this.view.renderUserInfo(user);
        } catch (e) {
            console.error(e);
            this.notifications.error(e.message);
        }
    }

    loadGroups = async (user) => {
        try {
            this.view.renderGroups(user.groups);
        } catch (e) {
            console.error(e);
            this.notifications.error(e.message);
        }
    }

    onViewGroupMembers = async (groupId = 0) => {
        try {
            const group = await fetchGroupMembers(groupId);
            this.view.showMembersModal(group.members);
        } catch (e) {
            console.error(e);
            this.notifications.error(e.message);
        }
    }

    onSaveProfileBtnClick = async () => {
        try {
            const userDto = this.view.prepareUserInfoApi();
            const result = await updateUser(userDto.id, userDto);
            if (result.success) {
                this.init().catch(console.error);
                this.notifications.success("Сохранено");
            } else {
                this.notifications.error(result.errors);
            }
        } catch (e) {
            console.error(e);
            this.notifications.error(e.message);
        }
    }
}