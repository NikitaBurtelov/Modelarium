import { apiFetch } from "./http";

const USER_API = import.meta.env.VITE_USER_API;

export async function createUser({ request, file }) {
    const formData = new FormData();

    formData.append(
        "request",
        new Blob([JSON.stringify(request)], { type: "application/json" })
    );

    if (file) {
        formData.append("file", file);
    }

    return apiFetch(`${USER_API}/api/user/create`, {
        method: "POST",
        body: formData,
    });
}

export async function getUsers(userIds = []) {
    return apiFetch(`${USER_API}/api/user/`, {
        method: "POST",
        body: {
            request: {
                userIds,
            },
        },
    });
}

export async function getUserById(userId) {
    return apiFetch(`${USER_API}/api/user/${userId}`);
}