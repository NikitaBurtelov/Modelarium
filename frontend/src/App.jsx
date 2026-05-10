import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import Layout from "./components/Layout";
import RegisterPage from "./pages/RegisterPage";
import ProjectsPage from "./pages/ProjectsPage";
import ArtistsPage from "./pages/ArtistsPage";
import CreatePostPage from "./pages/CreatePostPage";
import PostPage from "./pages/PostPage";

export default function App() {
  return (
      <Routes>
          <Route element={<Layout />}>
              <Route path="/" element={<Navigate to="/projects" replace />} />
              <Route path="/register" element={<RegisterPage />} />
              <Route path="/projects" element={<ProjectsPage />} />
              <Route path="/artists" element={<ArtistsPage />} />
              <Route path="/create-post" element={<CreatePostPage />} />
              <Route path="/posts/:postId" element={<PostPage />}/>
          </Route>
      </Routes>
  );
}