import {post} from "../utils/http.js";

export async function login(username, password) {
    let data = { username, password };

    return post("/auth/login", data, {});
}