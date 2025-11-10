import {createSlot, fetchAllSlots, fetchSlotsByPeriod, updateSlot} from "../../api/slotApi.js";
import {SlotsTable} from "../components/slotsTable.js";
import {fetchAllGroups} from "../../api/groupApi.js";
import {SlotModal} from "../components/slotModal.js";

document.addEventListener('DOMContentLoaded', function () {
    const page = new Lots();
    page.init().catch(console.error);
});

export class Lots {
    constructor() {
        // Track current view and offset
        this.currentView = 'week';
        this.currentOffset = 0;
        this.weekViewBtn = document.getElementById('weekViewBtn');
        this.monthViewBtn = document.getElementById('monthViewBtn');

        //button handlers
        if (this.weekViewBtn) {
            this.weekViewBtn.onclick = () => {
                this.onWeekBtnClick().catch(console.error);
            }
        }
        if (this.monthViewBtn) {
            this.monthViewBtn.onclick = () => {
                this.onMonthBtnClick().catch(console.error);
            }
        }

        this.currentDateRange = document.getElementById('currentDateRange');

        //modal
        this.slotModal = new SlotModal({
            selector: 'newSlotModal',
            dateSelector: 'slotDate',
            timeSelector: 'slotTimeStart',
            slotDuration: 'slotDuration',
            groupSelector: 'slotGroup',
            saveSlotBtn: 'saveSlotBtn',
            newSlotForm: 'newSlotForm',
            onSave: this.onSaveSlotBtnClick
        });

        //selectors
        this.weekNextSelector = document.querySelector('.btn-next-week')
        this.weekPrevSelector = document.querySelector('.btn-prev-week');

        if (this.weekNextSelector) {
            this.weekNextSelector.onclick = () => {
                this.onNextSelectorClick().catch(console.error);
            }
        }

        if (this.weekPrevSelector) {
            this.weekPrevSelector.onclick = () => {
                this.onPrevSelectorClick().catch(console.error);
            }
        }

        //table
        this.lotsTable = new SlotsTable(document.getElementById('timeSlotsTable'));

        // Handle time slot clicks
        let that = this;
        document.addEventListener('click', function (e) {
            const timeSlot = e.target.closest('.time-slot');
            if (timeSlot) {
                if (timeSlot.classList.contains('booked')) {
                    that.showSlotModal(timeSlot).catch(console.error);
                } else {
                   that.showSlotModal(timeSlot).catch(console.error);
                }
            }
        });

        this.lots = [];
    }

    init = async () => {
        // Initialize the calendar
        this.loadLots().catch(console.error);
    }

    loadLots = async () => {
        try {
            if (this.currentView === 'week') {
                const range =  this.lotsTable.getWeekRange(this.currentOffset);

                this.lots = await fetchSlotsByPeriod(range.startIso, range.endIso);
            } else {
                this.lots = await fetchAllSlots();
            }
            await this.initCalendar(this.currentView, this.currentOffset);
        } catch (error) {
            console.error(error);
        }
    }

    onPrevSelectorClick = async () => {
        this.currentOffset--;
        await this.initCalendar(this.currentView, this.currentOffset);
    }

    onNextSelectorClick = async () => {
        this.currentOffset++;
        await this.initCalendar(this.currentView, this.currentOffset);
    }

    onSaveSlotBtnClick = async () => {
        if (!this.slotModal.validateForm()){
            return null;
        }

        try {
            const slotDto = this.slotModal.getSlotForApi();
            if (slotDto.id == null) {
                await createSlot(slotDto);
            } else {
                await updateSlot(slotDto.id, slotDto);
            }
            //close modal
            this.slotModal.close();
            //reinit calendar
            this.loadLots().catch(console.error);
        } catch (e) {
            console.error(e);
        }
    }

    onWeekBtnClick = async () => {
        if (this.currentView !== 'week') {
            this.currentView = 'week';
            this.currentOffset = 0;
            this.weekViewBtn.classList.add('active');
            this.monthViewBtn.classList.remove('active');
            await this.initCalendar(this.currentView, this.currentOffset);
        }
    }

    onMonthBtnClick = async () => {
        if (this.currentView !== 'month') {
            this.currentView = 'month';
            this.currentOffset = 0;
            this.monthViewBtn.classList.add('active');
            this.weekViewBtn.classList.remove('active');
            await this.initCalendar(this.currentView, this.currentOffset);
        }
    }

    updateDateRange = async (view = 'week', offset = 0) => {
        if (view === 'week') {
            const range = this.lotsTable.getWeekRange(offset);
            const options = {day: 'numeric', month: 'long'};
            const startDate = range.startOfWeek.toLocaleDateString('ru-RU', options);
            const endDate = range.endOfWeek.toLocaleDateString('ru-RU', options);
            this.currentDateRange.textContent = `с ${startDate} по ${endDate}`;
        } else {
            const now = new Date();
            const monthDate = new Date(now.getFullYear(), now.getMonth() + offset, 1);
            this.currentDateRange.textContent = monthDate.toLocaleDateString('ru-RU', {month: 'long', year: 'numeric'});
        }
    }

    showSlotModal = async (timeSlot) => {
        try {
            const groups = await fetchAllGroups();
            this.slotModal.show(groups, timeSlot);
        } catch (error) {
            console.error(error);
        }
    }

    initCalendar = async (view = 'week', offset = 0) => {
        await this.updateDateRange(view, offset);

        if (view === 'week') {
            await this.lotsTable.renderWeek(this.lots, this.currentOffset);
        } else {
            await this.renderMonthView(offset);
        }

        // Ensure table headers are properly styled
        const headers = document.querySelectorAll('thead th');
        headers.forEach(header => {
            header.classList.add('sticky-header');
        });
    }

    renderMonthView = async (offset = 0) => {
        this.timeSlotsTable.innerHTML = '';
        this.timeSlotsTable.classList.add('month-view');
        const now = new Date();
        const date = new Date(now.getFullYear(), now.getMonth() + offset, 1);
        const daysInMonth = new Date(date.getFullYear(), date.getMonth() + 1, 0).getDate();
        const firstDay = new Date(date.getFullYear(), date.getMonth(), 1).getDay();

        // Create header row with weekday names
        const headerRow = document.createElement('tr');
        const weekdays = ['Пн', 'Вт', 'Ср', 'Чт', 'Пт', 'Сб', 'Вс'];

        weekdays.forEach(day => {
            const th = document.createElement('th');
            th.textContent = day;
            th.className = 'text-center fw-bold sticky-header';
            headerRow.appendChild(th);
        });

        this.timeSlotsTable.appendChild(headerRow);

        // Create calendar grid
        let day = 1;
        for (let i = 0; i < 6; i++) { // 6 weeks max
            if (day > daysInMonth) break;

            const weekRow = document.createElement('tr');

            for (let j = 0; j < 7; j++) {
                const dayCell = document.createElement('td');
                dayCell.className = 'month-day';

                if (i === 0 && j < firstDay - 1) {
                    // Empty cells before first day of month
                    dayCell.textContent = '';
                } else if (day > daysInMonth) {
                    // Empty cells after last day of month
                    dayCell.textContent = '';
                } else {
                    // Day number
                    const dayNumber = document.createElement('div');
                    dayNumber.className = 'day-number';
                    dayNumber.textContent = day;

                    if (new Date().getDate() === day &&
                        new Date().getMonth() === date.getMonth() &&
                        new Date().getFullYear() === date.getFullYear()) {
                        dayNumber.classList.add('today');
                    }

                    dayCell.appendChild(dayNumber);

                    // Add time slots for demo
                    if (Math.random() > 0.7) {
                        const slotCount = Math.floor(Math.random() * 3) + 1;
                        const slotsDiv = document.createElement('div');
                        slotsDiv.className = 'month-slots';

                        for (let s = 0; s < slotCount; s++) {
                            const slot = document.createElement('div');
                            slot.className = 'month-slot';

                            const hours = Math.floor(Math.random() * 8) + 8; // 8am-4pm
                            const minutes = Math.random() > 0.5 ? '00' : '30';
                            const duration = Math.random() > 0.7 ? '60' : '30';

                            slot.textContent = `${hours}:${minutes} (${duration} min)`;
                            slotsDiv.appendChild(slot);
                        }

                        dayCell.appendChild(slotsDiv);
                    }

                    day++;
                }

                weekRow.appendChild(dayCell);
            }

            this.timeSlotsTable.appendChild(weekRow);
        }
    }
}

// function showEditSlotModal(slotElement) {
//     const modal = new bootstrap.Modal(document.getElementById('newSlotModal'));
//     const slotInfo = slotElement.querySelector('.slot-info');
//
//     // Set modal title
//     document.querySelector('#newSlotModal .modal-title').textContent = 'Edit Time Slot';
//
//     // In a real app, you would load existing data from database
//     const group = slotInfo.querySelector('.slot-group').textContent;
//     const timeText = slotInfo.querySelector('.slot-time').textContent;
//
//     // Pre-fill form with existing data
//     document.getElementById('slotGroup').value = group;
//
//     // Show modal
//     modal.show();
//
//     // Change save button behavior for editing
//     const saveBtn = document.getElementById('saveSlotBtn');
//     const originalHandler = saveBtn.onclick;
//     saveBtn.onclick = function() {
//         // Save edited slot
//         const form = document.getElementById('newSlotForm');
//         if (!form.checkValidity()) {
//             form.classList.add('was-validated');
//             return;
//         }
//
//         // In real app, update database record here
//         const newGroup = document.getElementById('slotGroup').value;
//         slotInfo.querySelector('.slot-group').textContent = newGroup;
//
//         // Close modal and reset
//         modal.hide();
//         form.reset();
//         form.classList.remove('was-validated');
//         saveBtn.onclick = originalHandler;
//         document.querySelector('#newSlotModal .modal-title').textContent = 'Create New Time Slot';
//
//         alert('Time slot updated successfully!');
//     };
// }
