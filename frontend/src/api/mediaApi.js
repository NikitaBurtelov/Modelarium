import { apiFetch } from "./http";

const MEDIA_API = import.meta.env.VITE_MEDIA_API;

function normalizeMediaResponse(payload) {
    const mediaData = payload?.mediaData ?? payload ?? {};
    const result = {};

    if (Array.isArray(mediaData)) {
        mediaData.forEach((item) => {
            if (item?.id && item?.mediaUrl) {
                result[item.id] = item.mediaUrl;
            }
        });
        return result;
    }

    for (const value of Object.values(mediaData)) {
        if (Array.isArray(value)) {
            value.forEach((item) => {
                if (item?.id && item?.mediaUrl) {
                    result[item.id] = item.mediaUrl;
                }
            });
        } else if (value?.id && value?.mediaUrl) {
            result[value.id] = value.mediaUrl;
        }
    }

    return result;
}

export async function uploadMedia({ id, files }) {
    const formData = new FormData();
    formData.append("id", id);

    files.forEach((file) => {
        formData.append("files", file);
    });

    return apiFetch(`${MEDIA_API}/api/media/img`, {
        method: "POST",
        body: formData,
    });
}

export async function getMediaUrlsByIds(ids = []) {
    if (!ids.length) return {};

    const payload = await apiFetch(`${MEDIA_API}/api/media/img/urls/id`, {
        method: "POST",
        body: { id: ids },
    });

    return normalizeMediaResponse(payload);
}

export async function getMediaUrlsByKeys(keys = []) {
    if (!keys.length) return {};

    const payload = await apiFetch(`${MEDIA_API}/api/media/img/urls/key`, {
        method: "POST",
        body: { keys },
    });

    return normalizeMediaResponse(payload);
}