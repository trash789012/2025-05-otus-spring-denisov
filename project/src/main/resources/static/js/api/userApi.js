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

export async function updateUserAndRoles(id, user) {
    return put(`/user/${id}/roles`, user,
        {
            headers: {'Content-Type': 'application/json'},
        }
    );
}

export async function createUser(user) {
    return post(`/user`, user,
        {
            headers: {'Content-Type': 'application/json'},
        }
    );
}

export async function fetchAllRoles() {
    return get('/user/roles');
}

export async function fetchUserById(id) {
    return get(`/user/${id}`);
}

export async function deleteUser(id) {
    return del(`/user/${id}`);
}