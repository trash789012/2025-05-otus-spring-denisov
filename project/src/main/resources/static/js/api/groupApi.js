import {get, post} from "../utils/http.js";

export async function fetchAllGroups() {
    return get(`/group`);
}

export async function fetchGroupMembers(groupId) {
    return get(`/group/${groupId}/members`);
}