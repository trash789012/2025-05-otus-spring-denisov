export class ProfileTabs {
    constructor(params = {}) {
        this.firstName = document.getElementById(params.userName);
        this.lastName = document.getElementById(params.userLastName);
        this.shortDescription = document.getElementById(params.userDescription);

        this.saveProfileBtn = document.getElementById(params.saveProfileBtn);

        this.saveProfileBtn.onclick = () => {
            params.saveBtnEvent();
        }
    }

    renderUserInfo(user) {
        this.name = user.name;
        this.id = user.id;
        this.firstName.value = user.firstName;
        this.lastName.value = user.lastName;
        this.shortDescription.value = user.shortDescription;
    }

    prepareUserInfoApi() {
        return {
            id: this.id,
            name: this.name,
            firstName: this.firstName.value,
            lastName: this.lastName.value,
            shortDescription: this.shortDescription.value,
        };
    }
}