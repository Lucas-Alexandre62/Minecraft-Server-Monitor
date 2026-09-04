import { BrowserRouter, Routes, Route, useLocation } from "react-router-dom";
import Sidebar from "./components/Sidebar";
import Dashboard from "./pages/Dashboard";
import Servers from "./pages/Servers";
import ServerDetails from "./pages/ServerDetails";
import Alerts from "./pages/Alerts";
import Login from "./pages/Login";
import ProtectedRoute from "./components/ProtectedRoute";
import { Link } from "react-router-dom";
import "./App.css";

function AppRoutes() {
  const location = useLocation();
  const isLogin = location.pathname === "/login";

  return (
    <div className="layout">
      {!isLogin && <Sidebar />}

      <div className={isLogin ? "login-layout" : "main-content"}>
        <Routes>
          <Route path="/login" element={<Login />} />

          <Route
            path="/"
            element={
              <ProtectedRoute>
                <Dashboard />
              </ProtectedRoute>
            }
          />

          <Route
            path="/servers"
            element={
              <ProtectedRoute>
                <Servers />
              </ProtectedRoute>
            }
          />

          <Route
            path="/servers/:serverId"
            element={
              <ProtectedRoute>
                <ServerDetails />
              </ProtectedRoute>
            }
          />

          <Route
            path="/alerts"
            element={
              <ProtectedRoute>
                <Alerts />
              </ProtectedRoute>
            }
          />

          <Route
            path="*"
            element={
              <ProtectedRoute>
                <div className="page">
                  <div className="message">
                    Página não encontrada.
                  </div>
                  <Link to="/" className="refresh-button" style={{ marginTop: 16, display: "inline-block", textDecoration: "none" }}>
                    Voltar ao Dashboard
                  </Link>
                </div>
              </ProtectedRoute>
            }
          />
        </Routes>
      </div>
    </div>
  );
}

function App() {
  return (
    <BrowserRouter>
      <AppRoutes />
    </BrowserRouter>
  );
}

export default App;
