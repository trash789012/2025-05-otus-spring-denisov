export class UserCards {
    constructor() {
        this.container = document.getElementById('users-cards-grid');
    }

    render(users = []) {
        this.container.innerHTML = '';

        if (!users || users.length === 0) {
            this.renderEmptyState();
            return;
        }

        users.forEach(user => {
            const card = this.createUserCard(user);
            this.container.appendChild(card);
        });
    }

    createUserCard(user) {
        const card = document.createElement('div');
        card.className = 'entity-card';

        // Основной контент
        const cardContent = document.createElement('div');
        cardContent.className = 'card-content';

        cardContent.appendChild(this.createCardHeader(user));
        // cardContent.appendChild(this.createEmailRow(user));
        cardContent.appendChild(this.createStatusRow(user));

        // Кнопки действий (всегда внизу)
        const actionsRow = this.createActionsRow(user);

        card.appendChild(cardContent);
        card.appendChild(actionsRow);

        return card;
    }

    createCardHeader(user) {
        const cardHeader = document.createElement('div');
        cardHeader.className = 'card-header';

        const userInfo = document.createElement('div');
        userInfo.className = 'd-flex align-items-center';

        const avatar = document.createElement('div');
        avatar.className = 'user-avatar';
        avatar.textContent = this.getInitials(user.firstName || '', user.lastName || '');

        const textInfo = document.createElement('div');

        // Создаем ссылку для имени пользователя
        const userNameLink = document.createElement('a');
        userNameLink.href = `/admin/user/${user.id}`;
        userNameLink.className = 'card-title text-decoration-none';
        userNameLink.style.color = 'inherit';

        const userName = document.createElement('div');
        userName.className = 'card-title';
        userName.textContent = `${user.firstName} ${user.lastName}`;

        // Добавляем hover эффект
        userNameLink.addEventListener('mouseenter', () => {
            userNameLink.style.color = 'var(--primary-color)';
        });
        userNameLink.addEventListener('mouseleave', () => {
            userNameLink.style.color = 'inherit';
        });

        userNameLink.appendChild(userName);

        const userLogin = document.createElement('div');
        userLogin.className = 'card-meta';
        userLogin.textContent = `@${user.username || user.name}`;

        textInfo.appendChild(userNameLink);
        textInfo.appendChild(userLogin);
        userInfo.appendChild(avatar);
        userInfo.appendChild(textInfo);

        const roleBadge = document.createElement('span');
        roleBadge.className = this.getRoleBadgeClass(user.role);
        roleBadge.textContent = user.role || 'USER';

        cardHeader.appendChild(userInfo);
        cardHeader.appendChild(roleBadge);

        return cardHeader;
    }

    createEmailRow(user) {
        const emailRow = document.createElement('div');
        emailRow.className = 'card-meta mb-2';

        const emailIcon = document.createElement('i');
        emailIcon.className = 'bi bi-envelope me-1';

        const emailText = document.createTextNode(user.email || '');

        emailRow.appendChild(emailIcon);
        emailRow.appendChild(emailText);

        return emailRow;
    }

    createStatusRow(user) {
        const statusRow = document.createElement('div');
        statusRow.className = 'mb-2';

        const statusBadge = document.createElement('span');
        statusBadge.className = this.getStatusBadgeClass(user.status || '');
        statusBadge.textContent = this.getStatusText(user.status || '');

        statusRow.appendChild(statusBadge);

        return statusRow;
    }

    createActionsRow(user) {
        const actionsRow = document.createElement('div');
        actionsRow.className = 'card-actions mt-auto'; // mt-auto для прижатия к низу

        // const editButton = this.createEditButton(user.id);
        const deleteButton = this.createDeleteButton(user.id);

        // actionsRow.appendChild(editButton);
        actionsRow.appendChild(deleteButton);

        return actionsRow;
    }

    createEditButton(userId) {
        const editButton = document.createElement('button');
        editButton.className = 'btn btn-outline-primary btn-xs';

        editButton.addEventListener('click', () => this.onEditUser(userId));

        const editIcon = document.createElement('i');
        editIcon.className = 'bi bi-pencil me-1';

        const editText = document.createTextNode('Изменить');

        editButton.appendChild(editIcon);
        editButton.appendChild(editText);

        return editButton;
    }

    createDeleteButton(userId) {
        const deleteButton = document.createElement('button');
        deleteButton.className = 'btn btn-outline-danger btn-xs';
        deleteButton.addEventListener('click', () => {
            this.onDeleteUser(userId)
        });

        const deleteIcon = document.createElement('i');
        deleteIcon.className = 'bi bi-trash me-1';

        const deleteText = document.createTextNode('Удалить');

        deleteButton.appendChild(deleteIcon);
        deleteButton.appendChild(deleteText);

        return deleteButton;
    }

    getInitials(firstName, lastName) {
        if (!firstName || !lastName) return '??';
        return (firstName.charAt(0) + lastName.charAt(0)).toUpperCase();
    }

    getRoleBadgeClass(role) {
        const roleClasses = {
            'ADMIN': 'badge bg-primary badge-sm',
            'MODERATOR': 'badge bg-info badge-sm',
            'USER': 'badge bg-secondary badge-sm'
        };
        return roleClasses[role] || 'badge bg-secondary badge-sm';
    }

    getStatusBadgeClass(status) {
        const statusClasses = {
            'active': 'badge bg-success badge-sm',
            'inactive': 'badge bg-secondary badge-sm',
            'banned': 'badge bg-danger badge-sm',
            'pending': 'badge bg-warning badge-sm'
        };
        return statusClasses[status] || 'badge bg-secondary badge-sm';
    }

    getStatusText(status) {
        const statusTexts = {
            'active': 'Активен',
            'inactive': 'Неактивен',
            'banned': 'Заблокирован',
            'pending': 'Ожидает'
        };
        return statusTexts[status] || 'Неизвестно';
    }

    onEditUser(userId) {
        console.log('Редактирование пользователя:', userId);
        // Можно переопределить в дочернем классе
        this.dispatchEvent('edit', userId);
    }

    onDeleteUser(userId) {
        // Можно переопределить в дочернем классе
        this.dispatchEvent('delete', userId);
    }

    renderEmptyState() {
        const emptyMessage = document.createElement('div');
        emptyMessage.className = 'col-12 text-center py-5';

        const emptyIcon = document.createElement('i');
        emptyIcon.className = 'bi bi-people display-4 text-muted d-block mb-3';

        const emptyText = document.createElement('p');
        emptyText.className = 'text-muted';
        emptyText.textContent = 'Пользователи не найдены';

        emptyMessage.appendChild(emptyIcon);
        emptyMessage.appendChild(emptyText);
        this.container.appendChild(emptyMessage);
    }

    // Система событий для расширения функциональности
    addEventListener(event, callback) {
        this._events = this._events || {};
        this._events[event] = this._events[event] || [];
        this._events[event].push(callback);
    }

    dispatchEvent(event, data) {
        if (this._events && this._events[event]) {
            this._events[event].forEach(callback => callback(data));
        }
    }
}