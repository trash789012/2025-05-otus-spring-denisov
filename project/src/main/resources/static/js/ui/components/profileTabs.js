export class ProfileTabs {
    constructor(params = {}) {
        this.firstName = document.getElementById(params.userName);
        this.lastName = document.getElementById(params.userLastName);
        this.shortDescription = document.getElementById(params.userDescription);

        this.saveProfileBtn = document.getElementById(params.saveProfileBtn);

        this.saveProfileBtn.onclick = () => {
            params.saveBtnEvent();
        }

        this.viewGroupMembersEvt = params.viewGroupMembersEvt;
    }

    renderUserInfo(user) {
        this.name = user.name;
        this.id = user.id;
        this.firstName.value = user.firstName;
        this.lastName.value = user.lastName;
        this.shortDescription.value = user.shortDescription;
    }

    renderGroups(groups = []) {
        const groupsContainer = document.getElementById('userGroups');
        groupsContainer.innerHTML = ''; // Очищаем контейнер

        if (!groups || groups.length === 0) {
            const col = document.createElement('div');
            col.className = 'col-12';

            const alert = document.createElement('div');
            alert.className = 'alert alert-info text-center';

            const icon = document.createElement('i');
            icon.className = 'bi bi-info-circle me-2';

            alert.appendChild(icon);
            alert.appendChild(document.createTextNode('У вас пока нет групп'));

            col.appendChild(alert);
            groupsContainer.appendChild(col);
            return;
        }

        groups.forEach(group => {
            const col = document.createElement('div');
            col.className = 'col-md-4';

            const card = document.createElement('div');
            card.className = 'card h-100 shadow-sm';

            const cardBody = document.createElement('div');
            cardBody.className = 'card-body';

            // Название группы
            // {
            const groupNameLink = document.createElement('a');
            groupNameLink.href = `/profile/group/${group.id}`;
            groupNameLink.className = 'text-decoration-none';

            const groupName = document.createElement('h6');
            groupName.className = 'fw-bold mb-1 text-primary';
            groupName.textContent = group.name;

            // Обработчик клика по названию группы
            // groupNameLink.addEventListener('click', (e) => {
            //     e.preventDefault(); // Предотвращаем переход по ссылке если href="#"
            //     this.viewGroupDetails(group.id); // Новый метод для просмотра деталей группы
            // });

            groupNameLink.appendChild(groupName);

            // Жанр
            const genre = document.createElement('p');
            genre.className = 'text-muted small mb-2';
            genre.textContent = `Жанр: ${group.genre || 'Не указан'}`;

            // Количество участников
            const memberCount = document.createElement('p');
            memberCount.className = 'text-secondary small mb-3';
            memberCount.textContent = `Участников: ${group.memberIds?.length || 0}`;

            // Кнопка просмотра
            const viewButton = document.createElement('button');
            viewButton.className = 'btn btn-outline-primary btn-sm w-100 view-group';
            viewButton.setAttribute('data-group-id', group.id);

            const buttonIcon = document.createElement('i');
            buttonIcon.className = 'bi bi-eye me-1';

            viewButton.appendChild(buttonIcon);
            viewButton.appendChild(document.createTextNode('Посмотреть участников'));

            // Добавляем обработчик события
            let that = this;
            viewButton.addEventListener('click', function() {
                let groupId = this.getAttribute('data-group-id');
                that.viewGroupMembersEvt(groupId);
            });

            // Собираем карточку
            // cardBody.appendChild(groupName);
            cardBody.appendChild(groupNameLink);
            cardBody.appendChild(genre);
            cardBody.appendChild(memberCount);
            cardBody.appendChild(viewButton);

            card.appendChild(cardBody);
            col.appendChild(card);
            groupsContainer.appendChild(col);
        });
    }

    renderGroupMembers(members = []) {
        const membersList = document.getElementById('groupMembersList');
        membersList.innerHTML = ''; // Очищаем список

        if (!members || members.length === 0) {
            const emptyMessage = document.createElement('li');
            emptyMessage.className = 'list-group-item text-center text-muted';
            emptyMessage.textContent = 'В группе пока нет участников';
            membersList.appendChild(emptyMessage);
            return;
        }

        members.forEach(member => {
            const listItem = document.createElement('li');
            listItem.className = 'list-group-item d-flex align-items-center';

            // Иконка пользователя
            const userIcon = document.createElement('i');
            userIcon.className = 'bi bi-person-circle text-primary fs-4 me-2';

            // Контейнер для информации
            const infoContainer = document.createElement('div');

            // Имя участника
            const memberName = document.createElement('div');
            memberName.className = 'fw-bold';
            memberName.textContent = `${member.firstName} ${member.lastName}` || member.name;

            // Роль/инструмент
            const memberRole = document.createElement('small');
            memberRole.className = 'text-muted';
            memberRole.textContent = member.role || member.shortDescription || 'Участник';

            // Собираем структуру
            infoContainer.appendChild(memberName);
            infoContainer.appendChild(memberRole);

            listItem.appendChild(userIcon);
            listItem.appendChild(infoContainer);

            membersList.appendChild(listItem);
        });
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

    showMembersModal(members = []) {
        const modal = new bootstrap.Modal(document.getElementById('groupMembersModal'));

        this.renderGroupMembers(members)

        modal.show();
    }
}