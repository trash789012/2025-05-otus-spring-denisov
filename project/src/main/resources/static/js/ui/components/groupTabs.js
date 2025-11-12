export class GroupTabs {
    constructor() {
        // Заполнение формы-->
        this.groupName = document.getElementById('groupName');
        this.groupTitle = document.getElementById('groupTitle');
        this.groupDescription = document.getElementById('groupDescription1');
        // document.getElementById('groupBreadcrumb').textContent = groupData.name;-->
        //  document.getElementById('groupName').value = groupData.name;-->
        // document.getElementById('groupDescription').value = groupData.description;-->
        //  document.getElementById('groupGenre').value = groupData.genre;-->
        //  document.getElementById('deleteGroupName').textContent = groupData.name;-->

        this.membersCount = document.getElementById('membersCount');

    }

    renderGroupInfo(group = {}) {
        this.groupTitle.textContent = group.name;
        this.groupName.value = group.name;
        this.groupDescription.value = group.description;

        this.membersCount.value = group.members.length;
    }
}