import {get} from "../utils/http.js";

export async function getUserData() {
    return get("/user/self");
}