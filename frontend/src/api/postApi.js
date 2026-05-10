import { apiFetch } from "./http";

const POST_API = import.meta.env.VITE_POST_API;

export async function createPost({ authorId, request, files = [] }) {
    const formData = new FormData();

    formData.append(
        "request",
        new Blob([JSON.stringify(request)], { type: "application/json" })
    );

    files.forEach((file) => {
        formData.append("files", file);
    });

    return apiFetch(`${POST_API}/api/post/create`, {
        method: "POST",
        headers: {
            "author-id": authorId,
        },
        body: formData,
    });
}

export async function getPostById(postId) {
    return apiFetch(`${POST_API}/api/post/${postId}`);
}

export async function deletePost(postId) {
    return apiFetch(`${POST_API}/api/post/${postId}`, {
        method: "DELETE",
    });
}

export async function getPostsByUserId(userId) {
    return apiFetch(`${POST_API}/api/post/user/${userId}`);
}