export class UserTabs {
    constructor(params= {}) {
        this.userInit = document.getElementById('userInit');
        this.userLogin = document.getElementById('userLogin');
        this.firstName = document.getElementById('firstName');
        this.lastName = document.getElementById('lastName');
        this.username = document.getElementById('username');

        this.rolesSelector = document.getElementById('userRoles');

        this.allRoles = params.allRoles;
    }

    renderMainInfo(user = {}) {
        this.userInit.textContent = `${user.firstName} ${user.lastName}`;
        this.userLogin.textContent = `@${user.name}`;
        this.firstName.value = user.firstName;
        this.lastName.value = user.lastName;
        this.username.value = user.name;
    }

    renderRolesSelector(roles= []) {
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
}
