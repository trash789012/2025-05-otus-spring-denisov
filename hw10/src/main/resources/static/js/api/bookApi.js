import {del, get} from '../utils/http.js';

export async function fetchBook(id) {
    return get(`/book/${id}`);
}

export async function fetchAllBooks() {
    return get('/book');
}

export async function createBook(bookData) {
    try {
        const response = await fetch(`/api/v1/book`, {
            method: 'POST',
            body: JSON.stringify(bookData),
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
        });

        if (!response.ok) {
            const errors = await response.json();
            console.error('Ошибка валидации:', errors);
            return { success: false, errors};
        }

        const book = await response.json();
        return { success: true, book };
    } catch (error) {
        console.error('Ошибка сети:', error);
        return { success: false, error: 'Network error' };
    }
}

export async function updateBook(id, bookData) {
    try {
        const response = await fetch(`/api/v1/book/${id}`, {
            method: 'PUT',
            body: JSON.stringify(bookData),
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
        });

        if (!response.ok) {
            const errors = await response.json();
            console.error('Ошибка валидации:', errors);
            return { success: false, errors};
        }

        const book = await response.json();
        return { success: true, book };
    } catch (error) {
        console.error('Ошибка сети:', error);
        return { success: false, error: 'Network error' };
    }
}

export async function deleteBook(id) {
    return del(`/book/${id}`);
}