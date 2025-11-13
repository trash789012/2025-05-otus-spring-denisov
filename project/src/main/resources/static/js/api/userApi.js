import {del, get, post, put} from "../utils/http.js";

export async function getUserData() {
    return get("/user/self");
}

export async function fetchAllUsersWithRoles() {
    return get("/user");
}

export async function updateUser(id, user) {
    return put(`/user/${id}`, user,
        {
            headers: {'Content-Type': 'application/json'},
        }
    );
}

export async function deleteUser(id) {
    return del(`/user/${id}`);
}