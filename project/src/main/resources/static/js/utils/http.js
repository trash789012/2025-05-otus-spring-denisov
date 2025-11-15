const API_BASE = '/api/v1'

export function getApiBase() {
    return API_BASE;
}

// Общая функция для обработки ошибок аутентификации/авторизации
function handleAuthErrors(status, data) {
    switch (status) {
        case 401:
            if (data.invalidCredentials) {
                // Неверные креды при логине
                throw {
                    type: 'INVALID_CREDENTIALS',
                    message: data.message,
                    details: data
                };
            } else {
                // Другие 401 ошибки
                localStorage.removeItem("token");
                window.location.href = "/login";
                return true;
            }
        case 403: // Запрещено - нет прав
            throw {
                type: 'ACCESS_DENIED',
                message: 'Доступ запрещен',
                details: data
            };

        default:
            return false;
    }
}

// Общая функция для обработки ответов
async function processResponse(response) {
    // Сначала пробуем получить данные ответа
    const data = await response.json().catch(() => null);

    // Обработка аутентификации/авторизации
    if (response.status === 401 || response.status === 403) {
        if (handleAuthErrors(response.status, data)) {
            return null;
        }
    }

    // Обработка редиректов
    if (data?.redirect) {
        window.location.href = data.redirect;
        return null;
    }

    if (!response.ok) {
        throw new Error(data?.message || `HTTP error! Status: ${response.status}`);
    }

    // Обработка пустого ответа
    if (response.status === 204 || response.headers.get('Content-Length') === '0') {
        return null;
    }

    return data;
}

export async function get(url, options = {}) {
    const response = await fetch(`${API_BASE}${url}`, {
        headers: {
            'Content-Type': 'application/json',
            ...getAuthHeader(),
            ...options.headers
        },
        ...options
    });

    const result = await processResponse(response);
    return result;
}

export async function post(url, data, options = {}) {
    try {
        const response = await fetch(`${API_BASE}${url}`, {
            method: 'POST',
            body: JSON.stringify(data),
            headers: {
                'Content-Type': 'application/json',
                ...getAuthHeader(),
                ...options.headers
            },
        });

        const result = await processResponse(response);

        if (result === null) {
            return { success: true };
        }

        return { success: true, result };
    } catch (error) {
        // Для 403 ошибок пробрасываем дальше
        if (error.message.includes('Доступ запрещен')) {
            throw error;
        }

        // Для остальных ошибок возвращаем стандартный формат
        return { success: false, errors: error.message };
    }
}

export async function put(url, data, options = {}) {
    try {
        const response = await fetch(`${API_BASE}${url}`, {
            method: 'PUT',
            body: JSON.stringify(data),
            headers: {
                'Content-Type': 'application/json',
                ...getAuthHeader(),
                ...options.headers
            }
        });

        const result = await processResponse(response);

        if (result === null) {
            return { success: true };
        }

        return { success: true, result };
    } catch (error) {
        // Для 403 ошибок пробрасываем дальше
        if (error.message.includes('Доступ запрещен')) {
            throw error;
        }

        // Для остальных ошибок возвращаем стандартный формат
        return { success: false, errors: error.message };
    }
}

export async function del(url, options = {}) {
    try {
        const response = await fetch(`${API_BASE}${url}`, {
            method: 'DELETE',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                ...getAuthHeader(),
                ...options.headers
            },
            ...options
        });

        const result = await processResponse(response);

        return { success: true, result: result || {} };
    } catch (error) {
        throw error;
    }
}

function getAuthHeader() {
    const token = localStorage.getItem('token');
    return token ? {Authorization: `Bearer ${token}`} : {};
}

// Дополнительная функция для проверки авторизации
export function checkAccess(error) {
    return error && error.message && error.message.includes('Доступ запрещен');
}