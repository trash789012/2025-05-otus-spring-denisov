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
        this.groupSelector = new GroupSelector(parameters.groupSelector);
        this.saveSlotBtn = document.getElementById(parameters.saveSlotBtn);

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
}