export function parseLastUrlParam() {
    const pathParts = window.location.pathname.split('/');
    return pathParts[pathParts.length - 1];
}

export function parseErrors(errors) {
    return errors
        .map(error => error.defaultMessage) // Извлекаем сообщения
        .join('\n');
}

// export function parseErrors(errors) {
//     const lines = [];
//
//     const add = (val) => {
//         if (val == null) return;
//
//         if (Array.isArray(val)) {
//             val.forEach(add);
//             return;
//         }
//
//         if (typeof val === 'object') {
//             Object.values(val).forEach(add);
//             return;
//         }
//
//         lines.push(String(val));
//     };
//
//     const input = Array.isArray(errors) ? errors : [errors];
//
//     input.forEach(err => {
//         if (typeof err === 'string') {
//             lines.push(err);
//         } else if (err && typeof err === 'object') {
//             Object.values(err).forEach(add);
//         } else if (err != null) {
//             lines.push(String(err));
//         }
//     });
//
//     return lines.join('\n');
// }