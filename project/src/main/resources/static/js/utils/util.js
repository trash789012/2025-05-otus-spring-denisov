import {get} from "./http.js";

export function parseLastUrlParam() {
    const pathParts = window.location.pathname.split('/');
    return pathParts[pathParts.length - 1];
}

export async function getCurrentUserRoles() {
    return await get('/user/self/roles');
}

let roles = [];

export async function isRoles() {
    if (roles.length === 0) {
        roles = await getCurrentUserRoles();
    }

    return {
        root: roles.includes('ROOT'),
        admin: roles.includes('ADMIN'),
        user: roles.includes('USER'),
    }
}