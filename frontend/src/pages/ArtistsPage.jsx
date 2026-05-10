import { useEffect, useState } from "react";
import { getUsers } from "../api/userApi";
import UserCard from "../components/UserCard";

export default function ArtistsPage() {
    const [users, setUsers] = useState([]);
    const [message, setMessage] = useState("Загрузка...");

    useEffect(() => {
        async function load() {
            try {
                const data = await getUsers([]);
                const items = data?.userData || [];
                setUsers(items);
                setMessage(items.length ? "" : "Пользователи не найдены");
            } catch (error) {
                setMessage(error.message || "Ошибка загрузки пользователей");
            }
        }

        load();
    }, []);

    return (
        <section className="page">
            <h1>Пользователи</h1>

            {message ? <p className="notice">{message}</p> : null}

            <div className="grid-cards">
                {users.map((user) => (
                    <UserCard key={user.id} user={user} />
                ))}
            </div>
        </section>
    );
}