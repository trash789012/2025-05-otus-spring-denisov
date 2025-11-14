export class Notification {
    constructor() {
        this.container = null;
        this.init();
    }

    init() {
        if (!document.getElementById('notification-container')) {
            this.container = document.createElement('div');
            this.container.id = 'notification-container';
            this.container.className = 'position-fixed bottom-0 end-0 p-3';
            this.container.style.zIndex = '1055';
            document.body.appendChild(this.container);
        } else {
            this.container = document.getElementById('notification-container');
        }
    }

    getTypeClass(type) {
        const typeMap = {
            'success': 'success',
            'error': 'danger',
            'warning': 'warning',
            'info': 'info'
        };
        return typeMap[type] || 'info';
    }

    show(message, type = 'info', autoHide = true, delay = 4000) {
        const notificationId = 'notif-' + Date.now();

        const notification = document.createElement('div');
        notification.id = notificationId;
        notification.className = `toast align-items-center text-bg-${this.getTypeClass(type)} border-0 mb-2`;
        notification.setAttribute('role', 'alert');
        notification.setAttribute('aria-live', 'assertive');
        notification.setAttribute('aria-atomic', 'true');

        const flexDiv = document.createElement('div');
        flexDiv.className = 'd-flex';

        const toastBody = document.createElement('div');
        toastBody.className = 'toast-body';
        toastBody.textContent = message;

        flexDiv.appendChild(toastBody);

        if (!autoHide) {
            const closeButton = document.createElement('button');
            closeButton.type = 'button';
            closeButton.className = 'btn-close btn-close-white me-2 m-auto';
            closeButton.setAttribute('data-bs-dismiss', 'toast');
            closeButton.setAttribute('aria-label', 'Close');
            flexDiv.appendChild(closeButton);
        }

        notification.appendChild(flexDiv);
        this.container.appendChild(notification);

        const bsToast = new bootstrap.Toast(notification, {
            autohide: autoHide,
            delay: autoHide ? delay : 0
        });

        bsToast.show();

        notification.addEventListener('hidden.bs.toast', () => {
            notification.remove();
        });

        return notificationId;
    }

    // Методы для быстрого вызова разных типов уведомлений
    success(message, autoHide = true, delay = 4000) {
        return this.show(message, 'success', autoHide, delay);
    }

    error(message, autoHide = true, delay = 5000) {
        return this.show(message, 'error', autoHide, delay);
    }

    warning(message, autoHide = true, delay = 5000) {
        return this.show(message, 'warning', autoHide, delay);
    }

    info(message, autoHide = true, delay = 3000) {
        return this.show(message, 'info', autoHide, delay);
    }

    // Метод для ручного закрытия уведомления по ID
    hide(notificationId) {
        const notification = document.getElementById(notificationId);
        if (notification) {
            const bsToast = bootstrap.Toast.getInstance(notification);
            if (bsToast) {
                bsToast.hide();
            }
        }
    }

    // Метод для очистки всех уведомлений
    clearAll() {
        const toasts = this.container.querySelectorAll('.toast');
        toasts.forEach(toast => {
            const bsToast = bootstrap.Toast.getInstance(toast);
            if (bsToast) {
                bsToast.hide();
            }
        });
    }
}