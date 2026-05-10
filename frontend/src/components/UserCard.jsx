export default function UserCard({ user }) {
    const avatar = user?.mediaData?.[0]?.mediaUrl;

    return (
        <article className="card user-card">
            <div className="user-card__avatar">
                {avatar ? (
                    <img src={avatar} alt={user.displayName || user.userName} />
                ) : (
                    <div className="avatar-placeholder">
                        {(user?.displayName || user?.userName || "?").slice(0, 1).toUpperCase()}
                    </div>
                )}
            </div>

            <div className="user-card__body">
                <h3>{user.displayName || user.userName}</h3>
                <p className="muted">@{user.userName}</p>
                <p>{user.bio}</p>
            </div>
        </article>
    );
}