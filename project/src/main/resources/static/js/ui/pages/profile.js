import {getCurrentUser} from "../../utils/http.js";

document.addEventListener('DOMContentLoaded', function () {
    const profilePage = new Profile();
    profilePage.init().catch(console.error);
});

export class Profile {
    constructor() {
        this.userName = document.getElementById("userName");
    }

    init = async () => {
        this.loadProfile().catch(console.error);
    }

    loadProfile = async () => {
        try {
            const user = await getCurrentUser();
            this.userName.value = user.name
        } catch (e) {
            console.error(e);
        }
    }
}