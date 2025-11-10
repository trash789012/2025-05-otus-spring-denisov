import {get, post} from "../utils/http.js";

export async function fetchAllGroups() {
    return get(`/group`);
}