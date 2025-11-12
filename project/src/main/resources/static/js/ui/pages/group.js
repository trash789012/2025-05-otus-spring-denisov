import {GroupTabs} from "../components/groupTabs.js";

document.addEventListener('DOMContentLoaded', function () {
    const groupPage = new Group();
    groupPage.init().catch(console.error);
});

export class Group {
    constructor() {
        this.groupTabView = new GroupTabs();
    }

    init = async () => {
        this.loadGroup().catch(console.error);
    }

    loadGroup = async () => {
        try {

        } catch (e) {
            console.error(e);
        }
    }
}