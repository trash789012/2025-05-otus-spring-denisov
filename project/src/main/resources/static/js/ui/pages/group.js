import {GroupTabs} from "../components/groupTabs.js";
import {fetchGroupMembersAndSlots} from "../../api/groupApi.js";
import {parseLastUrlParam} from "../../utils/util.js";

document.addEventListener('DOMContentLoaded', function () {
    const groupPage = new Group(parseLastUrlParam());
    groupPage.init().catch(console.error);
});

export class Group {
    constructor(groupId) {
        this.groupTabView = new GroupTabs();
        this.groupId = groupId;
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
}