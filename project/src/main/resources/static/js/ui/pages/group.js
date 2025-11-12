import {GroupTabs} from "../components/groupTabs.js";
import {deleteGroup, fetchGroupMembersAndSlots, updateGroupInfo} from "../../api/groupApi.js";
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
            deleteOkGroupInfoEvt: this.onDeleteGroupOkBtnClick
        });
    }

    init = async () => {
        this.loadGroup().catch(console.error);
    }

    loadGroup = async () => {
        try {
            const group = await fetchGroupMembersAndSlots(this.groupId);
            this.groupTabView.renderGroupInfo(group);
        } catch (e) {
            console.error(e);
        }
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

}