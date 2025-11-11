import {getUserData, updateUser} from "../../api/userApi.js";
import {ProfileTabs} from "../components/profileTabs.js";

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
        });
    }

    init = async () => {
        this.loadProfile().catch(console.error);
    }

    loadProfile = async () => {
        try {
            const user = await getUserData();
            this.view.renderUserInfo(user);
        } catch (e) {
            console.error(e);
        }
    }

    onSaveProfileBtnClick = async () => {
        try {
            const userDto = this.view.prepareUserInfoApi();
            const result = await updateUser(userDto.id, userDto);
            if (result.success) {
                this.loadProfile().catch(console.error);
            }
        } catch (e) {
            console.error(e);
        }
    }
}