export function formatDate(value) {
    if (!value) return "";
    const date = new Date(value);

    return new Intl.DateTimeFormat("ru-RU", {
        dateStyle: "medium",
        timeStyle: "short",
    }).format(date);
}