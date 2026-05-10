import { useLocation, useNavigate } from "react-router-dom";
import { useState } from "react";

export default function PostPage() {
    const location = useLocation();
    const navigate = useNavigate();

    const post = location.state?.post;
    const author = location.state?.author;

    const [currentImage, setCurrentImage] = useState(0);

    if (!post) {
        return (
            <section className="page">
                <p>Пост не найден</p>

                <button onClick={() => navigate("/projects")}>
                    Назад
                </button>
            </section>
        );
    }

    const media = post.media || [];

    return (
        <section className="page">
            <button
                onClick={() => navigate(-1)}
                style={{ marginBottom: 20 }}
            >
                ← Назад
            </button>

            <div className="post-page">
                {media.length > 0 && (
                    <div className="post-gallery">
                        <img
                            className="post-gallery__main"
                            src={media[currentImage]?.mediaUrl}
                            alt=""
                        />

                        {media.length > 1 && (
                            <div className="post-gallery__thumbs">
                                {media.map((item, index) => (
                                    <img
                                        key={item.id}
                                        src={item.mediaUrl}
                                        alt=""
                                        onClick={() =>
                                            setCurrentImage(index)
                                        }
                                        className={
                                            currentImage === index
                                                ? "active"
                                                : ""
                                        }
                                    />
                                ))}
                            </div>
                        )}
                    </div>
                )}

                <div className="post-info">
                    <h1>{post.description}</h1>

                    <p>
                        <strong>Автор:</strong>{" "}
                        {author?.displayName ||
                            author?.userName ||
                            post.authorId}
                    </p>

                    <p>
                        <strong>Дата:</strong>{" "}
                        {new Date(
                            post.createdAt
                        ).toLocaleString()}
                    </p>

                    {post.tags?.length > 0 && (
                        <>
                            <h3>Tags</h3>

                            <div className="chips">
                                {post.tags.map((tag) => (
                                    <span
                                        key={tag}
                                        className="chip"
                                    >
                                        #{tag}
                                    </span>
                                ))}
                            </div>
                        </>
                    )}

                    {post.mentions?.length > 0 && (
                        <>
                            <h3>Mentions</h3>

                            <div className="chips">
                                {post.mentions.map((mention) => (
                                    <span
                                        key={mention}
                                        className="chip chip--soft"
                                    >
                                        @{mention}
                                    </span>
                                ))}
                            </div>
                        </>
                    )}
                </div>
            </div>
        </section>
    );
}