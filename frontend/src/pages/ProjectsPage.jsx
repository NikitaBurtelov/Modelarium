import { useEffect, useMemo, useState } from "react";
import { getUsers } from "../api/userApi";
import { getPostsByUserId } from "../api/postApi";
import { getMediaUrlsByIds } from "../api/mediaApi";
import PostCard from "../components/PostCard";

function normalizePostsResponse(payload) {
    const data = payload?.body ?? payload?.data ?? payload;

    if (Array.isArray(data)) return data;
    if (Array.isArray(data?.postData)) return data.postData;
    if (Array.isArray(data?.posts)) return data.posts;
    if (data?.id) return [data];
    return [];
}

export default function ProjectsPage() {
    const [posts, setPosts] = useState([]);
    const [users, setUsers] = useState([]);
    const [message, setMessage] = useState("Загрузка...");

    const usersById = useMemo(() => {
        const map = new Map();
        users.forEach((user) => map.set(user.id, user));
        return map;
    }, [users]);

    useEffect(() => {
        async function load() {
            try {
                const usersRes = await getUsers([]);
                const loadedUsers = usersRes?.userData || [];
                setUsers(loadedUsers);

                const postsLists = await Promise.all(
                    loadedUsers.map(async (user) => {
                        try {
                            const res = await getPostsByUserId(user.id);
                            return normalizePostsResponse(res).map((post) => ({
                                ...post,
                                authorId: post.authorId || user.id,
                            }));
                        } catch {
                            return [];
                        }
                    })
                );

                const flatPosts = postsLists.flat();
                const mediaIds = [...new Set(flatPosts.flatMap((post) => post.media || []))];
                const mediaMap = await getMediaUrlsByIds(mediaIds);

                const enriched = flatPosts.map((post) => ({
                    ...post,
                    mediaUrls: (post.media || []).map((id) => mediaMap[id]).filter(Boolean),
                }));

                setPosts(enriched);
                setMessage(enriched.length ? "" : "Посты не найдены");
            } catch (error) {
                setMessage(error.message || "Ошибка загрузки постов");
            }
        }

        load();
    }, []);

    return (
        <section className="page">
            <h1>Projects</h1>

            {message ? <p className="notice">{message}</p> : null}

            <div className="grid-posts">
                {posts.map((post) => (
                    <PostCard
                        key={post.id}
                        post={post}
                        author={usersById.get(post.authorId)}
                        mediaUrls={post.mediaUrls}
                    />
                ))}
            </div>
        </section>
    );
}