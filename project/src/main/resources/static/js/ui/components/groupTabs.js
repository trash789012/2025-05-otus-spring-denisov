export class GroupTabs {
    constructor(groupId = 0, params = {}) {
        this.groupId = groupId;

        this.groupName = document.getElementById('groupName');
        this.groupTitle = document.getElementById('groupTitle');
        this.groupDescription = document.getElementById('groupDescription1');
        // document.getElementById('groupBreadcrumb').textContent = groupData.name;-->
        //  document.getElementById('groupName').value = groupData.name;-->
        // document.getElementById('groupDescription').value = groupData.description;-->
        //  document.getElementById('groupGenre').value = groupData.genre;-->

        //  document.getElementById('deleteGroupName').textContent = groupData.name;-->

        this.membersCount = document.getElementById('membersCount');

        this.saveGroupBtn = document.getElementById('saveGroupBtn');
        saveGroupBtn.addEventListener('click', () => {
            params.updateGroupInfoEvt();
        });
    }

    renderGroupInfo(group = {}) {
        this.groupTitle.textContent = group.name;
        this.groupName.value = group.name;
        this.groupDescription.value = group.description;

        this.membersCount.textContent = group.members.length;
    }

    prepareGroupForApi() {
        return {
            id: this.groupId,
            name: this.groupName.value,
            description: this.groupDescription.value,
        };
    }
}