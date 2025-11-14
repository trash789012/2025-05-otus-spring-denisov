import {GroupTabs} from "../components/groupTabs.js";
import {
    addMembersToGroup, createGroup,
    deleteGroup,
    deleteMemberFromGroup, fetchGroupMembers,
    getUsersBySearchTerm,
    updateGroupInfo
} from "../../api/groupApi.js";
import {parseLastUrlParam} from "../../utils/util.js";

document.addEventListener('DOMContentLoaded', function () {
    const groupPage = new Group(parseLastUrlParam());
    groupPage.init().catch(console.error);
});

export class Group {
    constructor(groupId) {
        this.groupTabView = new GroupTabs(groupId, {
            updateGroupInfoEvt: this.onSaveGroupInfoBtnClick,
            deleteGroupInfoEvt: this.onDeleteGroupInfoBtnClick,
            deleteOkGroupInfoEvt: this.onDeleteGroupOkBtnClick,
            deleteRememberFromGroupEvt: this.onRemoveMemberFromGroup,
            onUserSearchEvt: this.onUserSearch,
            onConfirmAddMemberEvt: this.onConfirmAddMember
        });
        this.setEditMode(groupId);
    }

    init = async () => {
        try {
            if (this.editMode) {
                const group = await fetchGroupMembers(this.groupId);
                this.loadGroup(group).catch(console.error);
                this.loadMembers(group).catch(console.error);
            }
        } catch (e) {
            console.error(e);
        }
    }

    loadGroup = async (group) => {
        this.groupTabView.renderGroupInfo(group);
    }

    loadMembers = async (group) => {
        this.groupTabView.renderMembersTable(group?.members);
    }

    onSaveGroupInfoBtnClick = async () => {
        if (!this.groupTabView.validateForm()) {
            return;
        }
        try {
            const groupForm = this.groupTabView.prepareGroupForApi();
            let result = {};
            if (this.editMode) {
                result = await updateGroupInfo(this.groupId, groupForm);
            } else {
                result = await createGroup(groupForm);
            }
            if (!result.success) {
                console.log(result.errors);
                return null;
            }
            this.groupId = result.result.id;
            if (!this.editMode) {
                window.history.go(-1);
                return;
            }
            this.setEditMode(this.groupId);
            this.init().catch(console.error);
        } catch (e) {
            console.error(e);
        }
    }

    onRemoveMemberFromGroup = async (memberId) => {
        try {
            const result = await deleteMemberFromGroup(this.groupId, memberId);
            if (result && result.result) {
                this.groupTabView.removeMemberRow(memberId);
            }
        } catch (e) {
            console.error(e);
        }
    }

    onDeleteGroupInfoBtnClick = async () => {
        this.groupTabView.showDeleteConfirm();
    }

    onDeleteGroupOkBtnClick = async () => {
        try {
            await deleteGroup(this.groupId);
            window.location.href = "/";
        } catch (e) {
            console.error(e);
        }
    }

    onUserSearch = async () => {
        try {
            const pattern = this.groupTabView.getSearchTerm();
            const candidates = await getUsersBySearchTerm(this.groupId, pattern);
            this.groupTabView.renderSearchedMembers(candidates);
        } catch (e) {
            console.error(e);
        }
    }

    onConfirmAddMember = async () => {
        try {
            const selected = this.groupTabView.getSelectedMemberIds();
            const response = await addMembersToGroup(this.groupId, selected);
            if (response.success) {
                //update ui from response
                this.groupTabView.closeAddMemberModal();
                this.groupTabView.renderGroupInfo(response.result);
                this.groupTabView.renderMembersTable(response.result.members);
            }
        } catch (e) {
            console.error(e);
        }
    }
    setEditMode(groupId) {
        if (groupId == null || groupId === 'new') {
            this.groupId = 0;
            this.editMode = false;
        } else {
            this.groupId = groupId;
            this.editMode = true;
        }
        if (!this.editMode) {
            this.groupTabView.toggleMembersTab(false);
        }
    }
}