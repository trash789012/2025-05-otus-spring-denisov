import {del, get, post, put} from "../utils/http.js";

export async function fetchAllGroups() {
    return get(`/group`);
}

export async function fetchGroupMembers(groupId) {
    return get(`/group/${groupId}/members`);
}

export async function fetchGroupMembersAndSlots(groupId) {
    return get(`/group/${groupId}/members-and-slots`);
}

export async function createGroup(groupForm) {
    return post(`/group`, groupForm, {
        headers: {
            "Content-Type": "application/json",
        }
    });
}

export async function updateGroupInfo(id, groupInfo) {
    return put(`/group/${id}`, groupInfo,
        {
            headers: {'Content-Type': 'application/json'},
        }
    );
}

export async function deleteGroup(id) {
    return del(`/group/${id}`);
}

export async function deleteMemberFromGroup(groupId, memberId) {
    return del(`/group/${groupId}/members/${memberId}`);
}

export async function getUsersBySearchTerm(groupId, searchTerm = '*') {
    let url = `/group/${groupId}/candidates`;
    const params = new URLSearchParams();

    params.append('searchTerm', searchTerm);

    if (params.toString()) {
        url += `?${params.toString()}`;
    }

    return get(url);
}

export async function addMembersToGroup(groupId, membersIds = []) {
    return post(`/group/${groupId}/members`, membersIds, {
        headers: {
            'Content-Type': 'application/json'
        },
    })
}