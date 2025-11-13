export class UserTabs {
    constructor(params = {}) {
        this.userInit = document.getElementById('userInit');
        this.userLogin = document.getElementById('userLogin');
        this.firstName = document.getElementById('firstName');
        this.lastName = document.getElementById('lastName');
        this.username = document.getElementById('username');

        this.rolesSelector = document.getElementById('userRoles');

        this.saveUserBtn = document.getElementById('saveUserBtn');
        this.saveUserBtn.onclick = () => {
            params.saveBtnEvent();
        }

        this.allRoles = params.allRoles;
        this.userId = 0;
    }

    renderMainInfo(user = {}) {
        this.userId = user.id;
        this.userInit.textContent = `${user.firstName} ${user.lastName}`;
        this.userLogin.textContent = `@${user.name}`;
        this.firstName.value = user.firstName;
        this.lastName.value = user.lastName;
        this.username.value = user.name;
    }

    renderRolesSelector(roles = []) {
        this.rolesSelector.innerHTML = '';

        this.allRoles.forEach(role => {
            const option = document.createElement('option');
            option.value = role;
            option.textContent = role;

            if (roles) {
                option.selected = roles.includes(role);
            }

            this.rolesSelector.appendChild(option);
        });
    }

    getSelectedRoles() {
        return Array.from(this.rolesSelector.selectedOptions).map(
            option => option.value
        );
    }

    setAllRoles(roles) {
        this.allRoles = roles;
    }

    prepareForApi() {
        return {
            id: this.userId,
            firstName: this.firstName.value,
            lastName: this.lastName.value,
            roles: this.getSelectedRoles()
        };
    }
}
