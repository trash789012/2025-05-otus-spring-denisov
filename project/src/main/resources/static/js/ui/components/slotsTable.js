export class SlotsTable {
    constructor(tableElement) {
        if (!tableElement) {
            throw new Error('SlotsTable requires a valid table element');
        }
        this.timeSlotsTable = tableElement;
    }

    async renderWeek(lots = [], offset = 0) {
        this.timeSlotsTable.innerHTML = '';
        this.timeSlotsTable.classList.remove('month-view');

        const daysOfWeek = ['Пн', 'Вт', 'Ср', 'Чт', 'Пт', 'Сб', 'Вс'];

        // Create header row with time slots

        const headerRow = document.createElement('tr');
        const emptyHeader = document.createElement('th');

        emptyHeader.textContent = 'День/Время';
        emptyHeader.className = 'text-center align-middle fw-bold sticky-header';
        headerRow.appendChild(emptyHeader);

        // Заголовки времени
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

        // Рассчёт текущей недели
        const weekRange = this.getWeekRange(offset);
        const options = {day: 'numeric', month: 'numeric'};

        for (let day = 0; day < 7; day++) {
            const row = document.createElement('tr');

            // Day cell
            const dayCell = document.createElement('th');
            dayCell.className = 'text-center align-middle fw-bold sticky-column';
            dayCell.textContent = '' + weekRange.startOfWeek.toLocaleDateString('ru-RU', options);
            dayCell.textContent += ' (' + daysOfWeek[day] + ')';
            row.appendChild(dayCell);

            // Генерация строк времени
            for (let timeIndex = 0; timeIndex < emptyTimeSlots.length; timeIndex++) {

                const searchTime = emptyTimeSlots[timeIndex];
                const searchDateFormatted = weekRange.startOfWeek.toISOString().split('T')[0];
                const searchDateTime = `${searchDateFormatted}T${searchTime}:00`;

                const foundSlot = lots.find((slot) => {
                    return slot.startTime === searchDateTime
                });

                const timeCell = this.createCell(foundSlot, searchDateTime);

                if (foundSlot) {
                    const start = new Date(foundSlot.startTime);
                    const end = new Date(foundSlot.endTime);
                    const durationMinutes = (end - start) / 60000;
                    const durationSlots = durationMinutes / 30;

                    timeCell.colSpan = durationSlots + 1;

                    timeIndex += durationSlots;
                    row.appendChild(timeCell);
                    continue;
                }

                row.appendChild(timeCell);
            }

            this.timeSlotsTable.appendChild(row);

            weekRange.startOfWeek.setDate(weekRange.startOfWeek.getDate() + 1);
        }

    }

    createCell(slot = null, dateTime) {
        const timeCell = document.createElement('td');
        timeCell.className = 'time-slot position-relative';

        if  (dateTime && !slot) {
            timeCell.dataset.start = dateTime;
            timeCell.dataset.end = null;
            timeCell.dataset.duration = 1;
        }

        if (slot) {
            timeCell.classList.add('booked');

            const slotInfo = document.createElement('div');
            slotInfo.className = 'slot-info';

            const groupSpan = document.createElement('span');
            groupSpan.className = 'slot-group';
            groupSpan.textContent = slot.group?.name;

            const startTime = new Date(slot.startTime).toLocaleTimeString('ru-RU', {
                hour: '2-digit',
                minute: '2-digit'
            });

            const endTime = new Date(slot.endTime).toLocaleTimeString('ru-RU', {
                hour: '2-digit',
                minute: '2-digit'
            });

            const timeSpan = document.createElement('span');
            timeSpan.className = 'slot-time d-block';
            timeSpan.textContent = `${startTime} - ${endTime}`;

            //duration
            const start = new Date(slot.startTime);
            const end = new Date(slot.endTime);
            const durationMinutes = (end - start) / 60000;
            const durationSlots = durationMinutes / 30;

            timeCell.colSpan = durationSlots + 1;

            timeCell.dataset.start = slot.startTime;
            timeCell.dataset.end = slot.endTime;
            timeCell.dataset.duration = timeCell.colSpan;

            //append cell elements
            slotInfo.appendChild(groupSpan);
            slotInfo.appendChild(timeSpan);

            timeCell.appendChild(slotInfo);
        }

        return timeCell
    }

    getWeekRange(offset = 0) {
        const now = new Date();
        const startOfWeek = new Date(now);
        // Базовая корректировка на понедельник

        const dayCorrection = now.getDay() === 0 ? -6 : 1 - now.getDay();
        startOfWeek.setDate(now.getDate() + dayCorrection + (offset * 7));

        const endOfWeek = new Date(startOfWeek);
        endOfWeek.setDate(startOfWeek.getDate() + 6);

        const startOfWeekIso = new Date(startOfWeek);
        startOfWeekIso.setHours(0, 0, 0, 0); // нижняя граница — 00:00:00

        const endOfWeekIso = new Date(endOfWeek);
        endOfWeekIso.setHours(23, 59, 59, 999); // верхняя граница — 23:59:59.999

        const toApiFormat = (date) => {
            const local = new Date(date.getTime() - date.getTimezoneOffset() * 60000);
            return local.toISOString().split('.')[0];
        };

        return {
            startOfWeek,
            endOfWeek,
            startIso: toApiFormat(startOfWeekIso),
            endIso: toApiFormat(endOfWeekIso),
        };
    }
}