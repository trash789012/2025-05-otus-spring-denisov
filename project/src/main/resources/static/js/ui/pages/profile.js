import {getUserData} from "../../api/userApi.js";

document.addEventListener('DOMContentLoaded', function () {
    const profilePage = new Profile();
    profilePage.init().catch(console.error);
});

export class Profile {
    constructor() {
        this.firstName = document.getElementById("userName");
        this.lastName = document.getElementById("userLastName");
        this.shortDescription = document.getElementById("userDescription");
    }

    init = async () => {
        this.loadProfile().catch(console.error);
    }

    loadProfile = async () => {
        try {
            const user = await getUserData();
            this.firstName.value = user.firstName;
            this.lastName.value = user.lastName;
            this.shortDescription.value = user.shortDescription;
        } catch (e) {
            console.error(e);
        }
    }
}