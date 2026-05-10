import { useEffect, useMemo, useState } from "react";
import { getPosts } from "../api/postApi.js";
import PostCard from "../components/PostCard";

const POST_API = import.meta.env.VITE_POST_API;

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
                const res = await fetch(`${POST_API}/api/post`);
                if (!res.ok) {
                    throw new Error(`Ошибка ${res.status}`);
                }
                const data = await res.json();

                const items = data.items || [];
                const enriched = items.map((post) => ({
                    ...post,
                    mediaUrls: (post.media || []).map((m) => m.mediaUrl),
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
            {message && <p className="notice">{message}</p>}
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
