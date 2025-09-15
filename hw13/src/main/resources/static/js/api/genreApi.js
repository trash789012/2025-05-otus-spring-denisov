import {del, get, post, put} from "../utils/http.js";

export async function fetchAllGenres() {
    return get('/genre')
}

export async function fetchGenre(id) {
    return get(`/genre/${id}`);
}

export async function createGenre(genre) {
    return post('/genre', genre,
        {
            headers: {
                'Accept': 'application/json'
            }
        });
}

export async function updateGenre(id, genre) {
    return put(`/genre/${id}`, genre,
        {
            headers: {
                'Accept': 'application/json'
            }
        });
}

export async function deleteGenre(id) {
    return del(`/genre/${id}`);
}