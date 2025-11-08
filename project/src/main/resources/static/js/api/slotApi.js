import {get} from "../utils/http.js";

export async function fetchAllSlots() {
    return get(`/slot`);
}