export async function apiFetch(url, { method = "GET", body, headers = {} } = {}) {
    const options = {
        method,
        headers: {
            Accept: "application/json",
            ...headers,
        },
    };

    if (body instanceof FormData) {
        options.body = body;
    } else if (body !== undefined) {
        options.headers["Content-Type"] = options.headers["Content-Type"] || "application/json";
        options.body = typeof body === "string" ? body : JSON.stringify(body);
    }

    const response = await fetch(url, options);
    const text = await response.text();

    let data = null;
    if (text) {
        try {
            data = JSON.parse(text);
        } catch {
            data = text;
        }
    }

    if (!response.ok) {
        const message =
            (typeof data === "string" && data) ||
            data?.message ||
            data?.error ||
            response.statusText ||
            "Request failed";
        throw new Error(message);
    }

    return data;
}