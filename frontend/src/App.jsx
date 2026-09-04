import { BrowserRouter, Routes, Route } from "react-router-dom";
import Sidebar from "./components/Sidebar";
import Dashboard from "./pages/Dashboard";
import Servers from "./pages/Servers";
import ServerDetails from "./pages/ServerDetails";
import Alerts from "./pages/Alerts";
import Login from "./pages/Login";
import ProtectedRoute from "./components/ProtectedRoute";
import "./App.css";

function App() {
  return (
    <BrowserRouter>
      <div className="layout">
        <Sidebar />

        <div className="main-content">
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
                  </div>
                </ProtectedRoute>
              }
            />
          </Routes>
        </div>
      </div>
    </BrowserRouter>
  );
}

export default App;
