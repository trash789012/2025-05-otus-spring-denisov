import {isRoles} from "../../utils/util.js";

document.addEventListener('DOMContentLoaded', function () {
    const menuPage = new Menu();
    menuPage.init().catch(console.error);
});

export class Menu {
    constructor() {
        this.adminPoint = document.getElementById('adminPoint');
    }

    init = async () => {
        const perm = await isRoles();
        if (!perm.admin && !perm.root) {
            this.adminPoint.hidden = true;
        }
    }
}