export function parseLastUrlParam() {
    const pathParts = window.location.pathname.split('/');
    return pathParts[pathParts.length - 1];
}

export function parseErrors(errors) {
    return errors
        .map(error => error.defaultMessage) // Извлекаем сообщения
        .join('\n');
}