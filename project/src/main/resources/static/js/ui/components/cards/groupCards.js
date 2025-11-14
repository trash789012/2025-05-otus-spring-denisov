export class GroupCards {
    constructor() {
        this.container = document.getElementById('group-cards-grid');
    }

    render(groups) {
        this.container.innerHTML = '';

        if (!groups || groups.length === 0) {
            this.renderEmptyState();
            return;
        }

        groups.forEach(group => {
            const card = this.createGroupCard(group);
            this.container.appendChild(card);
        });
    }

    createGroupCard(group) {
        const card = document.createElement('div');
        card.className = 'entity-card';
        card.setAttribute('data-group-id', group.id);

        // Основной контент
        const cardContent = document.createElement('div');
        cardContent.className = 'card-content';

        cardContent.appendChild(this.createCardHeader(group));
        cardContent.appendChild(this.createGenreRow(group));
        // cardContent.appendChild(this.createMembersRow(group));

        // Кнопки действий
        const actionsRow = this.createActionsRow(group);

        card.appendChild(cardContent);
        card.appendChild(actionsRow);

        return card;
    }

    createCardHeader(group) {
        const cardHeader = document.createElement('div');
        cardHeader.className = 'card-header';

        const groupInfo = document.createElement('div');

        // Ссылка на название группы
        const groupNameLink = document.createElement('a');
        groupNameLink.href = `/admin/group/${group.id}`;
        groupNameLink.className = 'card-title text-decoration-none';
        groupNameLink.style.color = 'inherit';
        groupNameLink.style.cursor = 'pointer';

        const groupName = document.createElement('div');
        groupName.textContent = group.name;

        // Hover эффект
        groupNameLink.addEventListener('mouseenter', () => {
            groupNameLink.style.color = 'var(--primary-color)';
        });
        groupNameLink.addEventListener('mouseleave', () => {
            groupNameLink.style.color = 'inherit';
        });

        groupNameLink.appendChild(groupName);

        groupInfo.appendChild(groupNameLink);

        // const statusBadge = document.createElement('span');
        // statusBadge.className = this.getStatusBadgeClass(group.status);
        // statusBadge.textContent = this.getStatusText(group.status);

        cardHeader.appendChild(groupInfo);
        // cardHeader.appendChild(statusBadge);

        return cardHeader;
    }

    createGenreRow(group) {
        const genreRow = document.createElement('div');
        genreRow.className = 'card-meta mb-2';

        const genreIcon = document.createElement('i');
        genreIcon.className = 'bi bi-tag me-1';

        const genreText = document.createTextNode(group.genre || 'Не указан');

        genreRow.appendChild(genreIcon);
        genreRow.appendChild(genreText);

        return genreRow;
    }

    createMembersRow(group) {
        const membersRow = document.createElement('div');
        membersRow.className = 'card-meta mb-2';

        const membersIcon = document.createElement('i');
        membersIcon.className = 'bi bi-people me-1';

        const membersCount = group.membersCount || group.members?.length || 0;
        const membersText = document.createTextNode(
            this.getMembersText(membersCount)
        );

        membersRow.appendChild(membersIcon);
        membersRow.appendChild(membersText);

        return membersRow;
    }

    createActionsRow(group) {
        const actionsRow = document.createElement('div');
        actionsRow.className = 'card-actions';

        // const editButton = this.createEditButton(group.id);
        const deleteButton = this.createDeleteButton(group.id);

        // actionsRow.appendChild(editButton);
        actionsRow.appendChild(deleteButton);

        return actionsRow;
    }

    createEditButton(groupId) {
        const editButton = document.createElement('button');
        editButton.className = 'btn btn-outline-primary btn-xs';
        editButton.addEventListener('click', () => this.onEditGroup(groupId));

        const editIcon = document.createElement('i');
        editIcon.className = 'bi bi-pencil me-1';

        const editText = document.createTextNode('Изменить');

        editButton.appendChild(editIcon);
        editButton.appendChild(editText);

        return editButton;
    }

    createDeleteButton(groupId) {
        const deleteButton = document.createElement('button');
        deleteButton.className = 'btn btn-outline-danger btn-xs';
        deleteButton.addEventListener('click', () => this.onDeleteGroup(groupId));

        const deleteIcon = document.createElement('i');
        deleteIcon.className = 'bi bi-trash me-1';

        const deleteText = document.createTextNode('Удалить');

        deleteButton.appendChild(deleteIcon);
        deleteButton.appendChild(deleteText);

        return deleteButton;
    }

    renderEmptyState() {
        const emptyMessage = document.createElement('div');
        emptyMessage.className = 'col-12 text-center py-5';

        const emptyIcon = document.createElement('i');
        emptyIcon.className = 'bi bi-music-note-beamed display-4 text-muted d-block mb-3';

        const emptyText = document.createElement('p');
        emptyText.className = 'text-muted';
        emptyText.textContent = 'Коллективы не найдены';

        emptyMessage.appendChild(emptyIcon);
        emptyMessage.appendChild(emptyText);
        this.container.appendChild(emptyMessage);
    }

    getStatusBadgeClass(status) {
        const statusClasses = {
            'active': 'badge bg-success badge-sm',
            'inactive': 'badge bg-secondary badge-sm',
            'pending': 'badge bg-warning badge-sm',
            'banned': 'badge bg-danger badge-sm'
        };
        return statusClasses[status] || 'badge bg-secondary badge-sm';
    }

    getStatusText(status) {
        const statusTexts = {
            'active': 'Активен',
            'inactive': 'Неактивен',
            'pending': 'Ожидание',
            'banned': 'Заблокирован'
        };
        return statusTexts[status] || 'Неизвестно';
    }

    getMembersText(count) {
        if (count === 0) return 'Нет участников';
        if (count === 1) return '1 участник';
        if (count >= 2 && count <= 4) return `${count} участника`;
        return `${count} участников`;
    }

    onEditGroup(groupId) {
        console.log('Редактирование коллектива:', groupId);
        this.dispatchEvent('edit', groupId);
    }

    onDeleteGroup(groupId) {
        // if (confirm('Вы уверены, что хотите удалить этот коллектив?')) {
        //     console.log('Удаление коллектива:', groupId);
        this.dispatchEvent('delete', groupId);
        // }
    }

    removeCard(groupId) {
        const card = document.querySelector(`[data-group-id="${groupId}"]`);

        // Добавляем класс для анимации исчезновения
        card.style.transition = 'all 0.3s ease';
        card.style.opacity = '0';
        card.style.transform = 'translateX(-100%)';
        card.style.maxHeight = '0';
        card.style.overflow = 'hidden';
        card.style.margin = '0';
        card.style.padding = '0';
        card.style.border = 'none';

        // Удаляем карточку после завершения анимации
        setTimeout(() => {
            card.remove();

            // Если контейнер пуст, показываем состояние "нет пользователей"
            if (this.container.children.length === 0) {
                this.renderEmptyState();
            }
        }, 300);

    }

    // Система событий
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