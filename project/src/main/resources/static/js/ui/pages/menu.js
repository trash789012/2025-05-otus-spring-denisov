document.addEventListener('DOMContentLoaded', function () {
    const menuPage = new Menu();
    menuPage.init().catch(console.error);
});

export class Menu {
    constructor() {
        this.adminPoint = document.getElementById('adminPoint');
    }

    init = async () => {

    }
}