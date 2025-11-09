import {fetchAllSlots} from "../api/slotApi.js";

document.addEventListener('DOMContentLoaded', function() {
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
        this.saveSlotBtn = document.getElementById('saveSlotBtn');

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
        if (this.saveSlotBtn) {
            this.saveSlotBtn.onclick = () => {
                this.onSaveSlotBtnClick().catch(console.error);
            }
        }

        //modal
        this.currentDateRange = document.getElementById('currentDateRange');
        this.newSlotModal = document.getElementById('newSlotModal');

        //form
        this.newSlotForm = document.getElementById('newSlotForm');
        this.slotDate = document.getElementById('slotDate');
        this.slotTime = document.getElementById('slotTime');

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
        this.timeSlotsTable = document.getElementById('timeSlotsTable');

        // Handle time slot clicks
        let that = this;
        document.addEventListener('click', function(e) {
            const timeSlot = e.target.closest('.time-slot');
            if (timeSlot) {
                // if (timeSlot.classList.contains('booked')) {
                //     showEditSlotModal(timeSlot);
                // } else {
                that.showNewSlotModal();
                // }
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
            this.lots = await fetchAllSlots();
            await this.initCalendar(this.currentView, this.currentOffset);
            console.log(this.lots);
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
        if (!this.newSlotForm.checkValidity()) {
            this.newSlotForm.classList.add('was-validated');
            return null;
        }

        // // In a real app, you would save to a database here
        // const date = this.slotDate.value;
        // const time = this.slotTime.value;
        // const duration = parseInt(document.getElementById('slotDuration').value);
        // const group = document.getElementById('slotGroup').value;
        //
        // console.log('New slot saved:', { date, time, duration, group });
        //
        // // Close modal and reset form
        // const modal = bootstrap.Modal.getInstance(document.getElementById('newSlotModal'));
        // modal.hide();
        // form.reset();
        // form.classList.remove('was-validated');
        //
        // // Refresh calendar to show new slot
        // initCalendar();
        //
        // // Show success message
        // alert('Time slot created successfully!');
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
            const range = await this.getWeekRange(offset);
            const options = { day: 'numeric', month: 'long' };
            const startDate = range.startOfWeek.toLocaleDateString('ru-RU', options);
            const endDate = range.endOfWeek.toLocaleDateString('ru-RU', options);
            this.currentDateRange.textContent = `с ${startDate} по ${endDate}`;
        } else {
            const now = new Date();
            const monthDate = new Date(now.getFullYear(), now.getMonth() + offset, 1);
            this.currentDateRange.textContent = monthDate.toLocaleDateString('ru-RU', { month: 'long', year: 'numeric' });
        }
    }

    getWeekRange = async (offset = 0) => {
        const now = new Date();
        const startOfWeek = new Date(now);

        // Базовая корректировка на понедельник
        const dayCorrection = now.getDay() === 0 ? -6 : 1 - now.getDay();
        startOfWeek.setDate(now.getDate() + dayCorrection + (offset * 7));

        const endOfWeek = new Date(startOfWeek);
        endOfWeek.setDate(startOfWeek.getDate() + 6);

        return { startOfWeek, endOfWeek };
    }

    showNewSlotModal = () => {
        const modal = new bootstrap.Modal(this.newSlotModal);
        modal.show();

        // Set default date to today
        this.slotDate.valueAsDate = new Date();
    }

    initCalendar = async (view = 'week', offset = 0) => {
        await this.updateDateRange(view, offset);
        this.timeSlotsTable.innerHTML = '';

        if (view === 'week') {
            await this.renderWeekView(offset);
        } else {
            await this.renderMonthView(offset);
        }

        // Ensure table headers are properly styled
        const headers = document.querySelectorAll('thead th');
        headers.forEach(header => {
            header.classList.add('sticky-header');
        });
    }

    renderWeekView = async (offset = 0) => {
        this.timeSlotsTable.innerHTML = '';
        this.timeSlotsTable.classList.remove('month-view');
        const daysOfWeek = ['Пн', 'Вт', 'Ср', 'Чт', 'Пт', 'Сб', 'Вс'];

        // Create header row with time slots
        const headerRow = document.createElement('tr');
        const emptyHeader = document.createElement('th');
        emptyHeader.textContent = 'День/Время';
        emptyHeader.className = 'text-center align-middle fw-bold sticky-header';
        headerRow.appendChild(emptyHeader);
        // Time slots headers
        const emptyTimeSlots = [];
        for (let hour = 8; hour <= 23; hour++) {
            for (let minute = 0; minute < 60; minute += 30) {
                const time = `${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}`;
                emptyTimeSlots.push(time);
                const th = document.createElement('th');
                th.textContent = time;
                th.className = 'text-center align-middle sticky-header';
                headerRow.appendChild(th);
            }
        }

        this.timeSlotsTable.appendChild(headerRow);

        // Create rows for each day
        //
        const weekRange = await this.getWeekRange(offset);
        const options = { day: 'numeric', month: 'numeric' };
        //
        for (let day = 0; day < 7; day++) {
            const row = document.createElement('tr');

            // Day cell
            const dayCell = document.createElement('th');
            dayCell.className = 'text-center align-middle fw-bold sticky-column';
            //
            dayCell.textContent = '' + weekRange.startOfWeek.toLocaleDateString('ru-RU', options);
            //
            dayCell.textContent += ' (' + daysOfWeek[day] + ')';

            row.appendChild(dayCell);

            // Time slot cells for this day
            for (let timeIndex = 0; timeIndex < emptyTimeSlots.length; timeIndex++) {
                const timeCell = document.createElement('td');
                timeCell.className = 'time-slot position-relative';

                const searchTime = emptyTimeSlots[timeIndex];
                const searchDateFormatted = weekRange.startOfWeek.toISOString().split('T')[0];
                const searchDateTime = `${searchDateFormatted}T${searchTime}:00`;

                const foundSlot = this.lots.find((slot) => {
                    return slot.startTime === searchDateTime
                });
                if (foundSlot) {
                    //нашли слот, добавим его
                    timeCell.classList.add('booked');

                    const randomGroup = foundSlot.bookedById;
                    // const randomGroup = groupNames[Math.floor(Math.random() * groupNames.length)];
                    const duration = Math.floor(Math.random() * 3) + 1; // 1-3 slots (30-90 minutes)

                    const slotInfo = document.createElement('div');
                    slotInfo.className = 'slot-info';

                    const groupSpan = document.createElement('span');
                    groupSpan.className = 'slot-group';
                    groupSpan.textContent = randomGroup;

                    const startTime = new Date(foundSlot.startTime).toLocaleTimeString('ru-RU', {
                        hour: '2-digit',
                        minute: '2-digit'
                    });

                    const endTime = new Date(foundSlot.endTime).toLocaleTimeString('ru-RU', {
                        hour: '2-digit',
                        minute: '2-digit'
                    });

                    const timeSpan = document.createElement('span');
                    timeSpan.className = 'slot-time d-block';
                    timeSpan.textContent = `${startTime} - ${endTime}`;

                    slotInfo.appendChild(groupSpan);
                    slotInfo.appendChild(timeSpan);
                    timeCell.appendChild(slotInfo);

                    // Set column span based on duration
                    // timeCell.colSpan = duration;
                    timeCell.colSpan = 1;

                    // Skip next slots that are covered by this booking
                    // timeIndex += (duration - 1);
                }
                row.appendChild(timeCell);
            }
            this.timeSlotsTable.appendChild(row);

            //
            weekRange.startOfWeek.setDate(weekRange.startOfWeek.getDate() + 1);
            //
        }
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
                    // if (Math.random() > 0.7) {
                    //     const slotCount = Math.floor(Math.random() * 3) + 1;
                    //     const slotsDiv = document.createElement('div');
                    //     slotsDiv.className = 'month-slots';
                    //
                    //     for (let s = 0; s < slotCount; s++) {
                    //         const slot = document.createElement('div');
                    //         slot.className = 'month-slot';
                    //
                    //         const hours = Math.floor(Math.random() * 8) + 8; // 8am-4pm
                    //         const minutes = Math.random() > 0.5 ? '00' : '30';
                    //         const duration = Math.random() > 0.7 ? '60' : '30';
                    //
                    //         slot.textContent = `${hours}:${minutes} (${duration} min)`;
                    //         slotsDiv.appendChild(slot);
                    //     }
                    //
                    //     dayCell.appendChild(slotsDiv);
                    // }

                    day++;
                }

                weekRow.appendChild(dayCell);
            }

            this.timeSlotsTable.appendChild(weekRow);
        }
    }
}

function getWeekNumber(date) {
    const firstDayOfYear = new Date(date.getFullYear(), 0, 1);
    const pastDaysOfYear = (date - firstDayOfYear) / 86400000;
    return Math.ceil((pastDaysOfYear + firstDayOfYear.getDay() + 1) / 7);
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
