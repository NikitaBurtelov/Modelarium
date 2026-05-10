import { useState } from "react";
import { createUser } from "../api/userApi";
import ImagePreview from "../components/ImagePreview";

export default function RegisterPage() {
    const [form, setForm] = useState({
        userName: "",
        email: "",
        passwordHash: "",
        displayName: "",
        bio: "",
        emailVerified: false,
    });
    const [avatarFile, setAvatarFile] = useState(null);
    const [loading, setLoading] = useState(false);
    const [message, setMessage] = useState("");

    function onChange(e) {
        const { name, value, type, checked } = e.target;
        setForm((prev) => ({
            ...prev,
            [name]: type === "checkbox" ? checked : value,
        }));
    }

    async function onSubmit(e) {
        e.preventDefault();
        setLoading(true);
        setMessage("");

        try {
            await createUser({
                request: {
                    userName: form.userName,
                    email: form.email,
                    passwordHash: form.passwordHash,
                    displayName: form.displayName,
                    avatarKey: avatarFile ? avatarFile.name : "avatar",
                    bio: form.bio,
                    emailVerified: form.emailVerified,
                },
                file: avatarFile,
            });

            setMessage("Пользователь создан");
            setForm({
                userName: "",
                email: "",
                passwordHash: "",
                displayName: "",
                bio: "",
                emailVerified: false,
            });
            setAvatarFile(null);
        } catch (error) {
            setMessage(error.message || "Ошибка регистрации");
        } finally {
            setLoading(false);
        }
    }

    return (
        <section className="page">
            <h1>Регистрация</h1>

            <form className="form" onSubmit={onSubmit}>
                <div className="grid-2">
                    <label>
                        Username
                        <input name="userName" value={form.userName} onChange={onChange} required />
                    </label>

                    <label>
                        Display name
                        <input name="displayName" value={form.displayName} onChange={onChange} required />
                    </label>

                    <label>
                        Email
                        <input name="email" type="email" value={form.email} onChange={onChange} required />
                    </label>

                    <label>
                        Password hash
                        <input
                            name="passwordHash"
                            value={form.passwordHash}
                            onChange={onChange}
                            required
                        />
                    </label>
                </div>

                <label>
                    Bio
                    <textarea name="bio" value={form.bio} onChange={onChange} rows="5" required />
                </label>

                <label className="checkbox">
                    <input
                        type="checkbox"
                        name="emailVerified"
                        checked={form.emailVerified}
                        onChange={onChange}
                    />
                    Email verified
                </label>

                <label>
                    Avatar
                    <input
                        type="file"
                        accept="image/*"
                        onChange={(e) => setAvatarFile(e.target.files?.[0] || null)}
                    />
                </label>

                <ImagePreview files={avatarFile ? [avatarFile] : []} />

                <button className="btn" type="submit" disabled={loading}>
                    {loading ? "Создаю..." : "Создать пользователя"}
                </button>

                {message && <p className="notice">{message}</p>}
            </form>
        </section>
    );
}