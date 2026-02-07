export class UserTabs {
    constructor(params = {}) {
        this.userId = 0;

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

        this.deleteUserBtn = document.getElementById('deleteUserBtn');
        this.deleteUserBtn.onclick = () => {
            params.deleteBtnEvent();
        }

        this.allRoles = params.allRoles;

        this.groupList = document.getElementById('userGroupList');

        this.password = document.getElementById('password');
        this.passwordArea = document.getElementById('passwordArea');
    }

    requiredPassword(required=true) {
        this.password.required = required;
        if (!required) {
            this.passwordArea.hidden = true;
        } else {
            this.passwordArea.hidden = false;
        }
    }

    enableLogin() {
        this.username.readOnly = false;
    }

    disableLogin() {
        this.username.readOnly = true;

    }

    renderMainInfo(user = {}) {
        this.userId = user.id;
        this.userInit.textContent = `${user.firstName} ${user.lastName}`;
        this.userLogin.textContent = `@${user.name}`;
        this.firstName.value = user.firstName;
        this.lastName.value = user.lastName;
        this.username.value = user.name;
    }

    renderUserGroups(groups=[]) {
        this.groupList.innerHTML = '';

        if (!groups || groups.length === 0) {
            const emptyMessage = document.createElement('div');
            emptyMessage.className = 'text-center py-4';

            const emptyIcon = document.createElement('i');
            emptyIcon.className = 'bi bi-people display-4 text-muted d-block mb-3';

            const emptyText = document.createElement('p');
            emptyText.className = 'text-muted';
            emptyText.textContent = 'Пользователь не состоит в коллективах';

            emptyMessage.appendChild(emptyIcon);
            emptyMessage.appendChild(emptyText);
            this.groupList.appendChild(emptyMessage);
            return;
        }

        groups.forEach(group => {
            const groupItem = this.createGroupItem(group);
            this.groupList.appendChild(groupItem);
        });
    }

    createGroupItem(group) {
        const groupItem = document.createElement('div');
        groupItem.className = 'group-item';

        // Левая часть - информация о группе
        const groupInfo = document.createElement('div');

        const groupName = document.createElement('div');
        groupName.className = 'group-name';
        groupName.textContent = group.name;

        const groupMeta = document.createElement('div');
        groupMeta.className = 'group-meta';
        groupMeta.textContent = `${this.getMembersText(group.members.length)}`;

        groupInfo.appendChild(groupName);
        groupInfo.appendChild(groupMeta);

        // Правая часть - роль пользователя
        const roleInfo = document.createElement('div');
        roleInfo.className = 'text-end';

        const userRole = document.createElement('div');
        userRole.className = 'text-muted small';
        userRole.textContent = group.userRole || 'Участник';

        roleInfo.appendChild(userRole);

        // Добавляем статус, если есть
        if (group.status && group.status !== 'active') {
            const statusBadge = document.createElement('span');
            statusBadge.className = this.getGroupStatusBadgeClass(group.status);
            statusBadge.textContent = this.getGroupStatusText(group.status);
            roleInfo.appendChild(statusBadge);
        }

        // Собираем карточку
        groupItem.appendChild(groupInfo);
        groupItem.appendChild(roleInfo);

        return groupItem;
    }

    getMembersText(count) {
        if (count === 0) return 'Нет участников';
        if (count === 1) return '1 участник';
        if (count >= 2 && count <= 4) return `${count} участника`;
        return `${count} участников`;
    }

    getGroupStatusBadgeClass(status) {
        const statusClasses = {
            'active': 'badge bg-success badge-sm ms-2',
            'pending': 'badge bg-warning badge-sm ms-2',
            'inactive': 'badge bg-secondary badge-sm ms-2',
            'banned': 'badge bg-danger badge-sm ms-2'
        };
        return statusClasses[status] || 'badge bg-secondary badge-sm ms-2';
    }

    getGroupStatusText(status) {
        const statusTexts = {
            'active': 'Активен',
            'pending': 'Ожидание',
            'inactive': 'Неактивен',
            'banned': 'Заблокирован'
        };
        return statusTexts[status] || 'Неизвестно';
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
            name: this.username.value,
            password: this.password.value,
            firstName: this.firstName.value,
            lastName: this.lastName.value,
            roles: this.getSelectedRoles()
        };
    }
}
