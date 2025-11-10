import {get, post} from "../utils/http.js";

export async function fetchAllSlots() {
    return get(`/slot`);
}

/**
 * Получить все слоты или слоты за указанный период
 * @param {string} [start] - начало периода в ISO формате (например, 2025-10-20T00:00:00)
 * @param {string} [end] - конец периода в ISO формате (например, 2025-10-26T23:59:59)
 */
export async function fetchSlotsByPeriod(startDate, endDate) {
    let url = '/slot';

    //собираем query параметры
    const params = new URLSearchParams();
    if (startDate) {
        params.append('start', startDate);
        params.append('end', endDate);
    }

    if (params.toString()) {
        url += `?${params.toString()}`;
    }

    return get(url);
}

export async function createSlot(slot) {
    return post(`/slot`, slot,
        {
            headers: {'Content-Type': 'application/json'},
        }
    );
}