import {del, get, post, put} from '../utils/http.js';

export async function fetchBook(id) {
    return get(`/book/${id}`);
}

export async function fetchAllBooks() {
    return get('/book');
}

export async function createBook(bookData) {
    return post('/book', bookData,
        {
            headers: {
                'Accept': 'application/json'
            }
        }
    );
}

export async function updateBook(id, bookData) {
    return put(`/book/${id}`, bookData,
        {
            headers: {
                'Accept': 'application/json'
            }
        });
}

export async function deleteBook(id) {
    return del(`/book/${id}`);
}