import {GroupTabs} from "../components/groupTabs.js";
import {fetchGroupMembersAndSlots, updateGroupInfo} from "../../api/groupApi.js";
import {parseLastUrlParam} from "../../utils/util.js";

document.addEventListener('DOMContentLoaded', function () {
    const groupPage = new Group(parseLastUrlParam());
    groupPage.init().catch(console.error);
});

export class Group {
    constructor(groupId) {
        this.groupId = groupId;
        this.groupTabView = new GroupTabs(groupId,  {
            updateGroupInfoEvt: this.onSaveGroupInfoBtnClick
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
        try {
            const groupForm = this.groupTabView.prepareGroupForApi();
            const result = await updateGroupInfo(this.groupId, groupForm);
            if (!result.success) {
                console.log(result.errors);
            }
        } catch (e) {
            console.error(e);
        }
    }
}