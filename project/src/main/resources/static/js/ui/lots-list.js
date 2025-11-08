
document.addEventListener('DOMContentLoaded', function() {
    // Track current view and offset
    let currentView = 'week';
    let currentOffset = 0;

    // Initialize the calendar
    initCalendar(currentView, currentOffset);

    // Set up view toggle buttons
    document.getElementById('weekViewBtn').addEventListener('click', () => {
        if (currentView !== 'week') {
            currentView = 'week';
            currentOffset = 0;
            document.getElementById('weekViewBtn').classList.add('active');
            document.getElementById('monthViewBtn').classList.remove('active');
            initCalendar(currentView, currentOffset);
        }
    });

    document.getElementById('monthViewBtn').addEventListener('click', () => {
        if (currentView !== 'month') {
            currentView = 'month';
            currentOffset = 0;
            document.getElementById('monthViewBtn').classList.add('active');
            document.getElementById('weekViewBtn').classList.remove('active');
            initCalendar(currentView, currentOffset);
        }
    });
    // Set up event listeners
    document.getElementById('newSlotBtn').addEventListener('click', showNewSlotModal);
    document.getElementById('saveSlotBtn').addEventListener('click', saveNewSlot);

    // Week navigation buttons
    document.querySelector('.btn-prev-week').addEventListener('click', () => {
        currentOffset--;
        initCalendar(currentView, currentOffset);
    });

    document.querySelector('.btn-next-week').addEventListener('click', () => {
        currentOffset++;
        initCalendar(currentView, currentOffset);
    });
// Handle time slot clicks
    document.addEventListener('click', function(e) {
        const timeSlot = e.target.closest('.time-slot');
        if (timeSlot) {
            if (timeSlot.classList.contains('booked')) {
                showEditSlotModal(timeSlot);
            } else {
                showNewSlotModal();
            }
        }
    });
});
function initCalendar(view = 'week', offset = 0) {
    updateDateRange(view, offset);
    const tableBody = document.getElementById('timeSlotsTable');
    tableBody.innerHTML = '';

    if (view === 'week') {
        renderWeekView(offset);
    } else {
        renderMonthView(offset);
    }

    // Ensure table headers are properly styled
    const headers = document.querySelectorAll('thead th');
    headers.forEach(header => {
        header.classList.add('sticky-header');
    });
}
function renderWeekView(offset = 0) {
    const tableBody = document.getElementById('timeSlotsTable');
    tableBody.innerHTML = '';
    tableBody.classList.remove('month-view');
    const daysOfWeek = ['Пн', 'Вт', 'Ср', 'Чт', 'Пт', 'Сб', 'Вс'];
    const timeSlots = [];

    // Generate time slots from 8:00 AM to 8:00 PM
    for (let hour = 8; hour <= 20; hour++) {
        for (let minute = 0; minute < 60; minute += 30) {
            timeSlots.push(`${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}`);
        }
    }

    // Create header row with time slots
    const headerRow = document.createElement('tr');
    const emptyHeader = document.createElement('th');
    emptyHeader.textContent = 'День/Время';
    emptyHeader.className = 'text-center align-middle fw-bold sticky-header';
    headerRow.appendChild(emptyHeader);

    timeSlots.forEach(time => {
        const th = document.createElement('th');
        th.textContent = time;
        th.className = 'text-center align-middle sticky-header';
        headerRow.appendChild(th);
    });

    tableBody.appendChild(headerRow);
    // Create rows for each day
    for (let day = 0; day < 7; day++) {
        const row = document.createElement('tr');

        // Day cell
        const dayCell = document.createElement('th');
        dayCell.className = 'text-center align-middle fw-bold sticky-column';
        dayCell.textContent = daysOfWeek[day];
        row.appendChild(dayCell);

        // Time slot cells for this day
        for (let timeIndex = 0; timeIndex < timeSlots.length; timeIndex++) {
            const timeCell = document.createElement('td');
            timeCell.className = 'time-slot position-relative';

            // Randomly book some slots for demo purposes
            if (Math.random() > 0.8) {
                timeCell.classList.add('booked');

                const groupNames = ['Team Alpha', 'Project Beta', 'Dev Group', 'QA Team', 'Design Crew'];
                const randomGroup = groupNames[Math.floor(Math.random() * groupNames.length)];
                const duration = Math.floor(Math.random() * 3) + 1; // 1-3 slots (30-90 minutes)

                const slotInfo = document.createElement('div');
                slotInfo.className = 'slot-info';

                const groupSpan = document.createElement('span');
                groupSpan.className = 'slot-group';
                groupSpan.textContent = randomGroup;

                const startTime = timeSlots[timeIndex];
                const endTime = timeSlots[Math.min(timeIndex + duration, timeSlots.length - 1)];

                const timeSpan = document.createElement('span');
                timeSpan.className = 'slot-time d-block';
                timeSpan.textContent = `${startTime} - ${endTime}`;

                slotInfo.appendChild(groupSpan);
                slotInfo.appendChild(timeSpan);
                timeCell.appendChild(slotInfo);

                // Set column span based on duration
                timeCell.colSpan = duration;

                // Skip next slots that are covered by this booking
                timeIndex += (duration - 1);
            }
            row.appendChild(timeCell);
        }
        tableBody.appendChild(row);
    }
}
function renderMonthView(offset = 0) {
    const tableBody = document.getElementById('timeSlotsTable');
    tableBody.innerHTML = '';
    tableBody.classList.add('month-view');
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

    tableBody.appendChild(headerRow);

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

        tableBody.appendChild(weekRow);
    }
}
function getWeekNumber(date) {
    const firstDayOfYear = new Date(date.getFullYear(), 0, 1);
    const pastDaysOfYear = (date - firstDayOfYear) / 86400000;
    return Math.ceil((pastDaysOfYear + firstDayOfYear.getDay() + 1) / 7);
}
function updateDateRange(view = 'week', offset = 0) {
    if (view === 'week') {
        const now = new Date();
        const startOfWeek = new Date(now);
        startOfWeek.setDate(now.getDate() - now.getDay() + 1 + (offset * 7)); // Monday

        const endOfWeek = new Date(startOfWeek);
        endOfWeek.setDate(startOfWeek.getDate() + 6); // Sunday
        const options = { day: 'numeric', month: 'long' };
        const startDate = startOfWeek.toLocaleDateString('ru-RU', options);
        const endDate = endOfWeek.toLocaleDateString('ru-RU', options);
        document.getElementById('currentDateRange').textContent = `с ${startDate} по ${endDate}`;
        // document.getElementById('weekTitle').textContent = 'Неделя';
    } else {
        const now = new Date();
        const monthDate = new Date(now.getFullYear(), now.getMonth() + offset, 1);
        document.getElementById('currentDateRange').textContent = monthDate.toLocaleDateString('ru-RU', { month: 'long', year: 'numeric' });
        // document.getElementById('weekTitle').textContent = 'Месяц';
    }
}
function showNewSlotModal() {
    const modal = new bootstrap.Modal(document.getElementById('newSlotModal'));
    modal.show();

    // Set default date to today
    const today = new Date();
    document.getElementById('slotDate').valueAsDate = today;
}
function saveNewSlot() {
    const form = document.getElementById('newSlotForm');
    if (!form.checkValidity()) {
        form.classList.add('was-validated');
        return;
    }

    // In a real app, you would save to a database here
    const date = document.getElementById('slotDate').value;
    const time = document.getElementById('slotTime').value;
    const duration = parseInt(document.getElementById('slotDuration').value);
    const group = document.getElementById('slotGroup').value;

    console.log('New slot saved:', { date, time, duration, group });

    // Close modal and reset form
    const modal = bootstrap.Modal.getInstance(document.getElementById('newSlotModal'));
    modal.hide();
    form.reset();
    form.classList.remove('was-validated');

    // Refresh calendar to show new slot
    initCalendar();

    // Show success message
    alert('Time slot created successfully!');
}

function showEditSlotModal(slotElement) {
    const modal = new bootstrap.Modal(document.getElementById('newSlotModal'));
    const slotInfo = slotElement.querySelector('.slot-info');

    // Set modal title
    document.querySelector('#newSlotModal .modal-title').textContent = 'Edit Time Slot';

    // In a real app, you would load existing data from database
    const group = slotInfo.querySelector('.slot-group').textContent;
    const timeText = slotInfo.querySelector('.slot-time').textContent;

    // Pre-fill form with existing data
    document.getElementById('slotGroup').value = group;

    // Show modal
    modal.show();

    // Change save button behavior for editing
    const saveBtn = document.getElementById('saveSlotBtn');
    const originalHandler = saveBtn.onclick;
    saveBtn.onclick = function() {
        // Save edited slot
        const form = document.getElementById('newSlotForm');
        if (!form.checkValidity()) {
            form.classList.add('was-validated');
            return;
        }

        // In real app, update database record here
        const newGroup = document.getElementById('slotGroup').value;
        slotInfo.querySelector('.slot-group').textContent = newGroup;

        // Close modal and reset
        modal.hide();
        form.reset();
        form.classList.remove('was-validated');
        saveBtn.onclick = originalHandler;
        document.querySelector('#newSlotModal .modal-title').textContent = 'Create New Time Slot';

        alert('Time slot updated successfully!');
    };
}
