import {del, get, post} from '../utils/http.js';

export async function fetchAuthor(id) {
    return get(`/author/${id}`);
}

export async function fetchAllAuthors() {
    return get('/author');
}

export async function createAuthor(author) {
    return post('/author', author,
        {
            headers: {
                'Accept': 'application/json'
            }
        }
    );
}

export async function deleteAuthor(id) {
    return del(`/author/${id}`);
}