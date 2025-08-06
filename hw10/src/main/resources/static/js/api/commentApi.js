import {del, get, post} from "../utils/http.js";

export async function fetchAllByBookId(bookId) {
    return get(`/book/${id}/comment`)
}

export async function createComment(bookId, comment) {
    return post(`/book/${bookId}/comment`, comment,
        {
            headers: {
                'Accept': 'application/json'
            }
        });
}

export async function deleteComment(bookId, commentId) {
    return del(`/book/${bookId}/comment/${commentId}`);
}