import {del, get, post, put} from '../utils/http.js';

export async function fetchBook(id) {
    return get(`/book/${id}`);
}

export async function fetchAllBooks() {
    return get('/book');
}

export async function createBook(book) {
    return post('/book', book,
        {
            headers: {
                'Accept': 'application/json'
            }
        }
    );
}

export async function updateBook(id, book) {
    return put(`/book/${id}`, book,
        {
            headers: {
                'Accept': 'application/json'
            }
        });
}

export async function deleteBook(id) {
    return del(`/book/${id}`);
}