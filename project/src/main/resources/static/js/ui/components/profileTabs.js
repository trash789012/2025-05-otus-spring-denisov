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
            const groupName = document.createElement('h6');
            groupName.className = 'fw-bold mb-1';
            groupName.textContent = group.name;

            // Жанр
            const genre = document.createElement('p');
            genre.className = 'text-muted small mb-2';
            genre.textContent = `Жанр: ${group.genre || 'Не указан'}`;

            // Количество участников
            const memberCount = document.createElement('p');
            memberCount.className = 'text-secondary small mb-3';
            memberCount.textContent = `Участников: ${group.memberCount || 0}`;

            // Кнопка просмотра
            const viewButton = document.createElement('button');
            viewButton.className = 'btn btn-outline-primary btn-sm w-100 view-group';
            viewButton.setAttribute('data-group-id', group.id);

            const buttonIcon = document.createElement('i');
            buttonIcon.className = 'bi bi-eye me-1';

            viewButton.appendChild(buttonIcon);
            viewButton.appendChild(document.createTextNode('Посмотреть участников'));

            // Добавляем обработчик события
            viewButton.addEventListener('click', function() {
                // viewGroupMembers(group.id);
            });

            // Собираем карточку
            cardBody.appendChild(groupName);
            cardBody.appendChild(genre);
            cardBody.appendChild(memberCount);
            cardBody.appendChild(viewButton);

            card.appendChild(cardBody);
            col.appendChild(card);
            groupsContainer.appendChild(col);
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
}