import {getUserData, updateUser} from "../../api/userApi.js";
import {ProfileTabs} from "../components/profileTabs.js";
import {fetchGroupMembers} from "../../api/groupApi.js";

document.addEventListener('DOMContentLoaded', function () {
    const profilePage = new Profile();
    profilePage.init().catch(console.error);
});

export class Profile {
    constructor() {
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
        }
    }

    loadProfile = async (user) => {
        try {
            this.view.renderUserInfo(user);
        } catch (e) {
            console.error(e);
        }
    }

    loadGroups = async (user) => {
        try {
            this.view.renderGroups(user.groups);
        } catch (e) {
            console.error(e);
        }
    }

    onViewGroupMembers = async (groupId = 0) => {
        try {
            const group = await fetchGroupMembers(groupId);
            this.view.showMembersModal(group.members);
        } catch (e) {
            console.error(e);
        }
    }

    onSaveProfileBtnClick = async () => {
        try {
            const userDto = this.view.prepareUserInfoApi();
            const result = await updateUser(userDto.id, userDto);
            if (result.success) {
                this.init().catch(console.error);
            }
        } catch (e) {
            console.error(e);
        }
    }
}