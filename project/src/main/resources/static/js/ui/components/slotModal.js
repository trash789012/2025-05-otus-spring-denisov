import {GroupSelector} from "./groupSelector.js";

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

        this.groupSelector = new GroupSelector(parameters.groupSelector);

        this.slotId = 0;

        this.saveSlotBtn.onclick = () => {
            parameters.onSave();
        }
    }

    showCreateNew(groups, timeSlotCell) {
        this.modal = new bootstrap.Modal(this.newSlotModal);

        this.groupSelector.render(groups)

        const startDateTime = new Date(timeSlotCell.dataset.start);

        //дата
        this.slotDate.value = startDateTime.toISOString().split('T')[0]; // yyyy-MM-dd
        this.slotDate.readOnly = true;

        //время начала
        this.slotTime.value = startDateTime.toLocaleTimeString('ru-RU', { hour: '2-digit', minute: '2-digit' });
        this.slotTime.readOnly = true;

        //продолжительность
        this.slotDuration.value = 120; //по дефолту
        // this.slotDuration.value = timeSlotCell.dataset.duration * 30 - 30;

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

    // calculateEndTime(startDateTime, duration) {
    //     const start = new Date(startDateTime);
    //     const end = new Date(start);
    //
    //     end.setMinutes(end.getMinutes() + duration);
    //
    //     // Возвращаем в том же ISO формате (без UTC-сдвига)
    //     return end.toISOString().slice(0, 19); // "2025-11-10T10:30:00"
    // }

    calculateEndTime(startDateTime, durationMinutes) {
        const start = new Date(startDateTime);
        const end = new Date(start.getTime() + durationMinutes * 60000);

        // Форматируем вручную в локальное ISO (YYYY-MM-DDTHH:mm:ss)
        const pad = n => n.toString().padStart(2, '0');
        return `${end.getFullYear()}-${pad(end.getMonth() + 1)}-${pad(end.getDate())}T${pad(end.getHours())}:${pad(end.getMinutes())}:${pad(end.getSeconds())}`;
    }

}