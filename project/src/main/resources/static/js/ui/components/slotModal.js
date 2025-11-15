import {GroupSelector} from "./groupSelector.js";
import {isRoles} from "../../utils/util.js";

export class SlotModal {
    constructor(parameters = {}) {
        this.newSlotModal = document.getElementById(parameters.selector);
        if (!this.newSlotModal) {
            throw new Error("Cannot find a selector" + selector);
        }

        this.slotDate = document.getElementById(parameters.dateSelector);
        this.slotTime = document.getElementById(parameters.timeSelector);
        this.slotDuration = document.getElementById(parameters.slotDuration);
        this.saveSlotBtn = document.getElementById(parameters.saveSlotBtn);

        this.deleteSlotBtn = document.getElementById(parameters.deleteSlotBtn);

        this.groupSelector = new GroupSelector(parameters.groupSelector);

        this.slotId = 0;

        this.newSlotForm = document.getElementById(parameters.newSlotForm);

        this.saveSlotBtn.onclick = () => {
            parameters.onSave();
        }

        this.deleteSlotBtn.onclick = () => {
            parameters.onDelete();
        }
    }

    show(groups, timeSlotCell, timeSlotDb = null) {
        const that = this;
        //id
        if (timeSlotDb) {
            this.slotId = timeSlotDb.id;
        } else {
            this.slotId = timeSlotCell.dataset.id;
        }

        if (this.slotId) {
            document.querySelector('#newSlotModal .modal-title').textContent = 'Редактировать слот';
            this.deleteSlotBtn.hidden = false;
        } else {
            document.querySelector('#newSlotModal .modal-title').textContent = 'Создать новый слот';
            this.deleteSlotBtn.hidden = true;
        }

        //init
        this.modal = new bootstrap.Modal(this.newSlotModal);

        //группы
        if (timeSlotDb) {
            this.groupSelector.render(groups, timeSlotDb.group?.id)
        } else {
            this.groupSelector.render(groups, timeSlotCell.dataset.groupId)
        }

        //дата
        let startDateTime = new Date(timeSlotCell.dataset.start);
        if (timeSlotDb) {
            startDateTime = new Date(timeSlotDb.startTime);
        }

        this.slotDate.value = startDateTime.toISOString().split('T')[0]; // yyyy-MM-dd
        this.slotDate.readOnly = true;

        //время начала
        this.slotTime.value = startDateTime.toLocaleTimeString('ru-RU', {hour: '2-digit', minute: '2-digit'});
        this.slotTime.readOnly = true;

        //продолжительность
        if (!this.slotId) {
            this.slotDuration.value = 120; //по дефолту
        } else {
            if (timeSlotDb) {
                const endDateTime = new Date(timeSlotDb.endTime);
                const durationMinutes = (endDateTime - startDateTime) / 60000;
                this.slotDuration.value = durationMinutes;
            } else {
                this.slotDuration.value = timeSlotCell.dataset.duration * 30 - 30;
            }
        }

        //показываем
        this.modal.show();
    }

    getSlotForApi() {
        const duration = this.slotDuration.value;
        const startTime = `${this.slotDate.value}T${this.slotTime.value}:00`
        const endTime = this.calculateEndTime(startTime, duration);
        const groupId = this.groupSelector.getValue();

        return {
            id: this.slotId,
            startTime: startTime,
            endTime: endTime,
            status: null,
            groupId: groupId,
        };
    }

    calculateEndTime(startDateTime, durationMinutes) {
        const start = new Date(startDateTime);
        const end = new Date(start.getTime() + durationMinutes * 60000);

        // Форматируем вручную в локальное ISO (YYYY-MM-DDTHH:mm:ss)
        const pad = n => n.toString().padStart(2, '0');
        return `${end.getFullYear()}-${pad(end.getMonth() + 1)}-${pad(end.getDate())}T${pad(end.getHours())}:${pad(end.getMinutes())}:${pad(end.getSeconds())}`;
    }

    close() {
        const modal = bootstrap.Modal.getInstance(this.newSlotModal);
        modal.hide();
        this.newSlotForm.reset();
        this.newSlotForm.classList.remove('was-validated');
    }

    validateForm() {
        if (!this.newSlotForm.checkValidity()) {
            this.newSlotForm.classList.add('was-validated');
            return false;
        }

        return true;
    }

}