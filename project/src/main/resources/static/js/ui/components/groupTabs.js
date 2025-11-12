export class GroupTabs {
    constructor(groupId = 0, params = {}) {
        this.groupId = groupId;

        this.groupName = document.getElementById('groupName');
        this.groupTitle = document.getElementById('groupTitle');
        this.groupDescription = document.getElementById('groupDescription1');
        //  document.getElementById('groupGenre').value = groupData.genre;-->
        this.deleteGroupName = document.getElementById('deleteGroupName');
        this.groupInfoForm = document.getElementById('groupInfoForm');
        this.membersCount = document.getElementById('membersCount');
        this.totalMembers = document.getElementById('totalMembers');

        this.saveGroupBtn = document.getElementById('saveGroupBtn');
        this.saveGroupBtn.addEventListener('click', () => {
            params.updateGroupInfoEvt();
        });

        //кнопка вызова диалога подтверждения удаления
        this.deleteGroupBtn = document.getElementById('deleteGroupBtn');
        this.deleteGroupBtn.addEventListener('click', () => {
            params.deleteGroupInfoEvt();
        })

        //кнопка которая подтверждает удаление группы
        this.confirmDeleteGroup = document.getElementById('confirmDeleteGroup');
        this.confirmDeleteGroup.addEventListener('click', () => {
            params.deleteOkGroupInfoEvt();
        });

        this.membersTable = document.getElementById('membersTable');
        this.deleteRememberFromGroupEvt = params.deleteRememberFromGroupEvt;

        this.currentMembers = []; // Храним текущий список участников

        this.addMemberBtn = document.getElementById('addMemberBtn');
        this.addMemberBtn.addEventListener('click', () => {
            this.showAddMembers();
        })

        //кнопка поиска пользователей
        this.searchUser = document.getElementById('searchUserBtn');
        this.searchUser.addEventListener('click', () => {
            params.onUserSearchEvt();
        })

        //searchTerm для поиска пользователей
        this.searchUser = document.getElementById('searchUser');

        //кнопка ок для выбранных к добавлению пользователей
        this.confirmAddMember = document.getElementById('confirmAddMember');
        this.confirmAddMember.addEventListener('click', () => {
            params.onConfirmAddMemberEvt();
        })

        this.addMemberModal = document.getElementById('addMemberModal');
    }

    renderGroupInfo(group = {}) {
        this.groupTitle.textContent = group.name;
        this.groupName.value = group.name;
        this.groupDescription.value = group.description;
        this.deleteGroupName.textContent = group.name;
        this.membersCount.textContent = group.members.length;
    }

    renderMembersTable(members = []) {
        this.currentMembers = members; // Сохраняем текущий список

        this.membersTable.innerHTML = ''; // Очищаем таблицу

        if (!members || members.length === 0) {
            const emptyRow = document.createElement('tr');

            const emptyCell = document.createElement('td');
            emptyCell.colSpan = 4;
            emptyCell.className = 'text-center text-muted py-4';

            const emptyIcon = document.createElement('i');
            emptyIcon.className = 'bi bi-people display-4 d-block mb-2';

            const emptyText = document.createTextNode('В коллективе пока нет участников');

            emptyCell.appendChild(emptyIcon);
            emptyCell.appendChild(emptyText);
            emptyRow.appendChild(emptyCell);
            this.membersTable.appendChild(emptyRow);

            return;
        }

        members.forEach(member => {
            const row = document.createElement('tr');
            row.id = `member-row-${member.id}`; // Добавляем ID для строки

            // Ячейка с информацией об участнике
            const memberCell = document.createElement('td');
            const memberContainer = document.createElement('div');
            memberContainer.className = 'd-flex align-items-center';

            const memberIcon = document.createElement('i');
            memberIcon.className = 'bi bi-person-circle text-primary fs-4 me-3';

            const memberInfo = document.createElement('div');

            const memberName = document.createElement('div');
            memberName.className = 'fw-bold';
            memberName.textContent = `${member?.firstName} ${member?.lastName}` || member.name;

            const memberId = document.createElement('small');
            memberId.className = 'text-muted';
            memberId.textContent = `ID: ${member.id}`;

            memberInfo.appendChild(memberName);
            memberInfo.appendChild(memberId);
            memberContainer.appendChild(memberIcon);
            memberContainer.appendChild(memberInfo);
            memberCell.appendChild(memberContainer);

            // Ячейка с ролью/инструментом
            const roleCell = document.createElement('td');
            roleCell.textContent = member.role || 'Не указана';

            // Ячейка со статусом
            const statusCell = document.createElement('td');
            const statusBadge = document.createElement('span');
            statusBadge.className = member.status === 'active' ? 'badge bg-success' : 'badge bg-warning';
            statusBadge.textContent = member.status === 'active' ? 'Активный' : 'Ожидание';
            statusCell.appendChild(statusBadge);

            // Ячейка с действиями
            const actionsCell = document.createElement('td');
            const removeButton = document.createElement('button');
            removeButton.className = 'btn btn-sm btn-outline-danger remove-member';
            removeButton.setAttribute('data-member-id', member.id);

            const removeIcon = document.createElement('i');
            removeIcon.className = 'bi bi-person-dash';

            removeButton.appendChild(removeIcon);
            actionsCell.appendChild(removeButton);

            // Добавляем обработчик события для кнопки удаления
            let that = this;
            removeButton.addEventListener('click', function () {
                const memberId = this.getAttribute('data-member-id');
                that.deleteRememberFromGroupEvt(memberId);
            });

            // Собираем строку
            row.appendChild(memberCell);
            row.appendChild(roleCell);
            row.appendChild(statusCell);
            row.appendChild(actionsCell);

            this.membersTable.appendChild(row);
        });

        this.updateMembersStats(members);
    }

    updateMembersStats(members) {
        const total = members.length;
        // const active = members.filter(m => m.status === 'active').length;
        // const pending = members.filter(m => m.status === 'pending').length;

        this.membersCount.textContent = total;
        this.totalMembers.textContent = total;
        // document.getElementById('activeMembers').textContent = active;
        // document.getElementById('pendingMembers').textContent = pending;
    }

    setTitle(title = "") {
        this.groupTitle.textContent = title;
    }

    prepareGroupForApi() {
        return {
            id: this.groupId,
            name: this.groupName.value,
            description: this.groupDescription.value,
        };
    }

    validateForm() {
        if (!this.groupInfoForm.checkValidity()) {
            this.groupInfoForm.classList.add('was-validated');
            return false;
        }

        this.groupInfoForm.classList.remove('was-validated');

        return true;
    }

    showDeleteConfirm() {
        const modal = new bootstrap.Modal(document.getElementById('confirmDeleteModal'));
        modal.show();
    }

    showAddMembers() {
        const modal = new bootstrap.Modal(this.addMemberModal);

        this.renderSearchedMembers(null);

        modal.show();
    }

    closeAddMemberModal() {
        const modal = bootstrap.Modal.getInstance(this.addMemberModal);
        modal.hide();
    }

    removeMemberRow(memberId = 0) {
        const rowToRemove = document.getElementById(`member-row-${memberId}`);

        if (rowToRemove) {
            // Анимация удаления (опционально)
            rowToRemove.style.opacity = '0';
            rowToRemove.style.transition = 'opacity 0.3s ease';

            setTimeout(() => {
                rowToRemove.remove();

                // Обновляем текущий список участников
                this.currentMembers = this.currentMembers.filter(member => member.id != memberId);

                // Если после удаления не осталось участников, показываем пустое состояние
                if (this.currentMembers.length === 0) {
                    this.renderMembersTable([]);
                    this.updateMembersStats(this.currentMembers);
                } else {
                    // Обновляем статистику
                    this.updateMembersStats(this.currentMembers);
                }
            }, 300);
        }
    }

    renderSearchedMembers(users = []) {
        const usersTable = document.getElementById('usersTable');
        usersTable.innerHTML = '';

        if (!users || users.length === 0) {
            const row = document.createElement('tr');
            const cell = document.createElement('td');
            cell.colSpan = 3;
            cell.className = 'text-center text-muted py-3';

            const icon = document.createElement('i');
            icon.className = 'bi bi-search me-2';

            const text = document.createTextNode('Пользователи не найдены');

            cell.appendChild(icon);
            cell.appendChild(text);
            row.appendChild(cell);
            usersTable.appendChild(row);
            return;
        }

        users.forEach(user => {
            const row = document.createElement('tr');

            // Ячейка выбора
            const selectCell = document.createElement('td');
            selectCell.className = 'text-center align-middle';

            const checkbox = document.createElement('input');
            checkbox.type = 'checkbox';
            checkbox.className = 'form-check-input user-select';
            checkbox.setAttribute('data-user-id', user.id);
            selectCell.appendChild(checkbox);

            selectCell.addEventListener('click', (e) => {
                // Предотвращаем двойное срабатывание если кликнули прямо на чекбокс
                if (e.target !== checkbox) {
                    checkbox.checked = !checkbox.checked;
                    // Триггерим событие change для чекбокса
                    checkbox.dispatchEvent(new Event('change', { bubbles: true }));
                }
            });

            // Ячейка пользователя
            const userCell = document.createElement('td');
            const userContainer = document.createElement('div');
            userContainer.className = 'd-flex align-items-center';

            const userIcon = document.createElement('i');
            userIcon.className = 'bi bi-person-circle text-primary me-2';

            const userInfo = document.createElement('div');

            const userName = document.createElement('div');
            userName.className = 'fw-semibold';
            userName.textContent = `${user.firstName} ${user.lastName}`;

            const userId = document.createElement('small');
            userId.className = 'text-muted';
            userId.textContent = `ID: ${user.id}`;

            userInfo.appendChild(userName);
            userInfo.appendChild(userId);
            userContainer.appendChild(userIcon);
            userContainer.appendChild(userInfo);
            userCell.appendChild(userContainer);

            // Ячейка email
            const emailCell = document.createElement('td');
            emailCell.textContent = user.email || '';

            // Собираем строку
            row.appendChild(selectCell);
            row.appendChild(userCell);
            row.appendChild(emailCell);

            usersTable.appendChild(row);
        });

    }

    getSearchTerm() {
        return this.searchUser.value;
    }

    getSelectedMemberIds() {
        const selectedCheckboxes = document.querySelectorAll('#usersTable .user-select:checked');
        const selectedIds = Array.from(selectedCheckboxes).map(checkbox => {
            return parseInt(checkbox.getAttribute('data-user-id'));
        });
        return selectedIds;
    }
}