import { Link, Route, Routes } from 'react-router-dom'
import LoginPage from './pages/LoginPage.jsx'
import RegisterPage from './pages/RegisterPage.jsx'
import ChatPage from './pages/ChatPage.jsx'

function NotFound() { return <main><h1>Page not found</h1><Link to="/login">Go to login</Link></main> }

export default function App() {
  return <Routes>
    <Route path="/" element={<LoginPage />} />
    <Route path="/login" element={<LoginPage />} />
    <Route path="/register" element={<RegisterPage />} />
    <Route path="/chat" element={<ChatPage />} />
    <Route path="*" element={<NotFound />} />
  </Routes>
}
