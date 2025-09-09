const API_BASE = '/api/v1'

export function getApiBase() {
    return API_BASE;
}

export async function get(url, options = {}) {
    const response = await fetch(`${API_BASE}${url}`, {
        headers: {
            'Content-Type': 'application/json',
            ...options.headers
        },
        ...options
    });

    if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`);
    }

    if (response.status === 204 || response.headers.get('Content-Length') === '0') {
        return null;
    }

    try {
        return await response.json();
    } catch (error) {
        return null;
    }
}

export async function post(url, data, options = {}) {
    try {
        const response = await fetch(`${API_BASE}${url}`, {
            method: 'POST',
            body: JSON.stringify(data),
            headers: {
                'Content-Type': 'application/json',
                ...options.headers
            },
        });

        if (!response.ok) {
            const errors = await response.json();
            console.error('Ошибка валидации:', errors);
            return { success: false, errors};
        }

        const result = await response.json();
        return { success: true, result };
    } catch (error) {
        throw new Error(`HTTP error! Status: ${response.status}`);
    }
}

export async function put(url, data, options = {}) {
    try {
        const response = await fetch(`${API_BASE}${url}`, {
            method: 'PUT',
            body: JSON.stringify(data),
            headers: {
                'Content-Type': 'application/json',
                ...options.headers
            }
        });

        if (!response.ok) {
            const errors = await response.json();
            console.error('Ошибка валидации:', errors);
            return { success: false, errors};
        }

        const result = await response.json();
        return { success: true, result };
    } catch (error) {
        throw new Error(`HTTP error! Status: ${response.status}`);
    }
}

export async function del(url) {
    const response = await fetch(`${API_BASE}${url}`, {
        method: 'DELETE',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        }
    });

    if (!response.ok) {
        const data = await response.json().catch(() => null);
        if (data?.redirect) {
            window.location.href = data.redirect;
            return;
        }

        throw new Error(`HTTP error! Status: ${response.status}`);
    }
}