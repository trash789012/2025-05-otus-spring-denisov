import {login} from "../../api/loginApi.js";
import {Notification} from "../../utils/notifications.js";

document.addEventListener('DOMContentLoaded', function () {
    const pageLogin = new Login();
    pageLogin.init().catch(console.error);
});

export class Login {
    constructor() {
        this.notifications = new Notification();
        this.userName = document.getElementById("username");
        this.password = document.getElementById("password");
        this.form = document.getElementById("loginForm");

        this.form.addEventListener("submit", (e) => {
            e.preventDefault();
            this.logIn().catch(console.error);
        })
    }

    init = async () => {
    }

    logIn = async () => {
        try {
            localStorage.removeItem("token");

            const userName = this.userName.value;
            const password = this.password.value;

            const response = await login(userName, password);
            if (!response.success) {
                this.notifications.error("Ошибка входа. Неверный пользователь или пароль", false);
                console.log(response.errors);
                return;
            }
            localStorage.setItem("token", response.result.token);
            window.location.href = "/";
        } catch (e) {
            console.error(e);
        }
    }
}