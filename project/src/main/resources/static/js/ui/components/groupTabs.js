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
        })
    }

    renderGroupInfo(group = {}) {
        this.groupTitle.textContent = group.name;
        this.groupName.value = group.name;
        this.groupDescription.value = group.description;
        this.deleteGroupName.textContent = group.name;
        this.membersCount.textContent = group.members.length;
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
}