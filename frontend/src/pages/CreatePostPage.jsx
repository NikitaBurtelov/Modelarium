import { useState } from "react";
import { createPost } from "../api/postApi";
import ImagePreview from "../components/ImagePreview";

export default function CreatePostPage() {
    const [authorId, setAuthorId] = useState("");
    const [description, setDescription] = useState("");
    const [tags, setTags] = useState("");
    const [mentions, setMentions] = useState("");
    const [files, setFiles] = useState([]);
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState("");

    async function onSubmit(e) {
        e.preventDefault();
        setLoading(true);
        setMessage("");

        try {
            await createPost({
                authorId,
                request: {
                    description,
                    tags: tags
                        .split(",")
                        .map((x) => x.trim())
                        .filter(Boolean),
                    mentions: mentions
                        .split(",")
                        .map((x) => x.trim())
                        .filter(Boolean),
                    files: [],
                },
                files,
            });

            setMessage("Пост создан");
            setDescription("");
            setTags("");
            setMentions("");
            setFiles([]);
        } catch (error) {
            setMessage(error.message || "Ошибка создания поста");
        } finally {
            setLoading(false);
        }
    }

    return (
        <section className="page">
            <h1>Создать пост</h1>

            <form className="form" onSubmit={onSubmit}>
                <label>
                    Author ID
                    <input
                        value={authorId}
                        onChange={(e) => setAuthorId(e.target.value)}
                        placeholder="uuid пользователя"
                        required
                    />
                </label>

                <label>
                    Description
                    <textarea
                        rows="5"
                        value={description}
                        onChange={(e) => setDescription(e.target.value)}
                        required
                    />
                </label>

                <div className="grid-2">
                    <label>
                        Tags, через запятую
                        <input value={tags} onChange={(e) => setTags(e.target.value)} />
                    </label>

                    <label>
                        Mentions, через запятую
                        <input value={mentions} onChange={(e) => setMentions(e.target.value)} />
                    </label>
                </div>

                <label>
                    Images
                    <input
                        type="file"
                        accept="image/*"
                        multiple
                        onChange={(e) => setFiles(Array.from(e.target.files || []))}
                    />
                </label>

                <ImagePreview files={files} />

                <button className="btn" type="submit" disabled={loading}>
                    {loading ? "Создаю..." : "Создать пост"}
                </button>

                {message && <p className="notice">{message}</p>}
            </form>
        </section>
    );
}