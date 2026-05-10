import { NavLink } from "react-router-dom";

const linkClass = ({ isActive }) =>
    `nav-link ${isActive ? "active" : ""}`;

export default function Navbar() {
    return (
        <header className="navbar">
            <div className="navbar__brand">MVP Gallery</div>

            <nav className="navbar__links">
                <NavLink to="/projects" className={linkClass}>
                    Posts
                </NavLink>
                <NavLink to="/artists" className={linkClass}>
                    Users
                </NavLink>
                <NavLink to="/register" className={linkClass}>
                    Register
                </NavLink>
                <NavLink to="/create-post" className={linkClass}>
                    Create post
                </NavLink>
            </nav>
        </header>
    );
}