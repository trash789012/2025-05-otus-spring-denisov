const API_BASE = '/api/v1'

export function getApiBase() {
    return API_BASE;
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

    if (response.status === 401 || response.status === 403) {
        localStorage.removeItem("token");
        window.location.href = "/login";
        return;
    }

    if (!response.ok) {
        const data = await response.json().catch(() => null);
        if (data?.redirect) {
            window.location.href = data.redirect;
            return;
        }

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
                ...getAuthHeader(),
                ...options.headers
            },
        });

        if (response.status === 401 || response.status === 403) {
            localStorage.removeItem("token");
            window.location.href = "/login";
            return;
        }

        if (!response.ok) {
            const data = await response.json().catch(() => null);
            if (data?.redirect) {
                window.location.href = data.redirect;
                return;
            }

            const errors = data;
            console.error('Ошибка:', errors);
            return {success: false, errors};
        }

        const result = await response.json();
        return {success: true, result};
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
                ...getAuthHeader(),
                ...options.headers
            }
        });

        if (response.status === 401 || response.status === 403) {
            localStorage.removeItem("token");
            window.location.href = "/login";
            return;
        }

        if (!response.ok) {
            const data = await response.json().catch(() => null);
            if (data?.redirect) {
                window.location.href = data.redirect;
                return;
            }

            const errors = data;
            console.error('Ошибка валидации:', errors);
            return {success: false, errors};
        }

        const result = await response.json();
        return {success: true, result};
    } catch (error) {
        throw new Error(`HTTP error! Status: ${response.status}`);
    }
}

export async function del(url) {
    const response = await fetch(`${API_BASE}${url}`, {
        method: 'DELETE',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            ...getAuthHeader(),
        }
    });

    if (response.status === 401 || response.status === 403) {
        localStorage.removeItem("token");
        window.location.href = "/login";
        return;
    }

    if (!response.ok) {
        const data = await response.json().catch(() => null);
        if (data?.redirect) {
            window.location.href = data.redirect;
            return;
        }

        throw new Error(`HTTP error! Status: ${response.status}`);
    }
}

export async function getCurrentUser() {
    return {
        id: 1,
        name: "admin"
    };
}

function getAuthHeader() {
    const token = localStorage.getItem('token');
    return token ? {Authorization: `Bearer ${token}`} : {};
}