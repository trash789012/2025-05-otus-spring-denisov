document.addEventListener('DOMContentLoaded', function () {
    const menuPage = new Menu();
    menuPage.init().catch(console.error);
});

export class Menu {
    constructor() {
        this.logoutBtn = document.getElementById('logoutBtn');

        this.logoutBtn.addEventListener("click", (e) => {
            e.preventDefault();
            localStorage.removeItem('token');
            window.location.href = '/login';
        })
    }

    init = async () => {
    }
}