import {GroupTabs} from "../components/groupTabs.js";
import {
    deleteGroup,
    deleteMemberFromGroup,
    fetchGroupMembersAndSlots,
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
        this.groupId = groupId;
        this.groupTabView = new GroupTabs(groupId, {
            updateGroupInfoEvt: this.onSaveGroupInfoBtnClick,
            deleteGroupInfoEvt: this.onDeleteGroupInfoBtnClick,
            deleteOkGroupInfoEvt: this.onDeleteGroupOkBtnClick,
            deleteRememberFromGroupEvt: this.onRemoveMemberFromGroup,
            onUserSearchEvt: this.onUserSearch
        });
    }

    init = async () => {
        try {
            const group = await fetchGroupMembersAndSlots(this.groupId);

            this.loadGroup(group).catch(console.error);
            this.loadMembers(group).catch(console.error);
        } catch (e) {
            console.error(e);
        }

    }

    loadGroup = async (group) => {
        this.groupTabView.renderGroupInfo(group);
    }

    loadMembers = async (group) => {
        this.groupTabView.renderMembersTable(group.members);
    }

    onSaveGroupInfoBtnClick = async () => {
        if (!this.groupTabView.validateForm()) {
            return;
        }
        try {
            const groupForm = this.groupTabView.prepareGroupForApi();
            const result = await updateGroupInfo(this.groupId, groupForm);
            if (!result.success) {
                console.log(result.errors);
                return null;
            }

            this.groupTabView.setTitle(groupForm.name);
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

}