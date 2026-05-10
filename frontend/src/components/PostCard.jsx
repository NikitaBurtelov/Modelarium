import { useNavigate } from "react-router-dom";
import { formatDate } from "../utils/formatDate";

export default function PostCard({ post, author, mediaUrls = [] }) {
    const navigate = useNavigate();

    function openPost() {
        navigate(`/posts/${post.id}`, {
            state: {
                post,
                author,
            },
        });
    }

    return (
        <article
            className="card post-card"
            onClick={openPost}
            style={{ cursor: "pointer" }}
        >
            <div className="post-card__media">
                {mediaUrls.length > 0 ? (
                    <img
                        src={mediaUrls[0]}
                        alt={post.description || "post"}
                    />
                ) : (
                    <div className="media-placeholder">
                        No image
                    </div>
                )}
            </div>

            <div className="post-card__body">
                <div className="post-card__topline">
                    <strong>
                        {author?.displayName ||
                            author?.userName ||
                            "Unknown user"}
                    </strong>

                    <span className="muted">
                        {formatDate(post.createdAt)}
                    </span>
                </div>

                <p>{post.description}</p>

                {(post.tags?.length > 0 ||
                    post.mentions?.length > 0) && (
                    <div className="chips">
                        {post.tags?.map((tag) => (
                            <span className="chip" key={tag}>
                                #{tag}
                            </span>
                        ))}

                        {post.mentions?.map((mention) => (
                            <span
                                className="chip chip--soft"
                                key={mention}
                            >
                                @{mention}
                            </span>
                        ))}
                    </div>
                )}
            </div>
        </article>
    );
}