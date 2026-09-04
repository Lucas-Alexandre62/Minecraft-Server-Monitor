import { NavLink, useNavigate } from "react-router-dom";
import { clearToken } from "../api";

function Sidebar() {
  const navigate = useNavigate();

  function handleLogout() {
    clearToken();

    navigate("/login", {
      replace: true,
    });
  }
  return (
    <aside className="sidebar">
      <div className="sidebar-logo">
        <div className="sidebar-logo-icon">M</div>

        <div>
          <strong>Minecraft</strong>
          <span>Monitor</span>
        </div>
      </div>

      <nav className="sidebar-nav">
        <NavLink
          to="/"
          end
          className={({ isActive }) =>
            isActive ? "nav-item active" : "nav-item"
          }
        >
          <span>◼</span>
          Dashboard
        </NavLink>

        <NavLink
          to="/servers"
          className={({ isActive }) =>
            isActive ? "nav-item active" : "nav-item"
          }
        >
          <span>◉</span>
          Servidores
        </NavLink>

        <NavLink
          to="/alerts"
          className={({ isActive }) =>
            isActive ? "nav-item active" : "nav-item"
          }
        >
          <span>⚑</span>
          Alertas
        </NavLink>
      </nav>

      <div className="sidebar-footer">
        <span>Monitoramento Minecraft</span>
        <small>v0.1.0</small>
        <div style={{ marginTop: 10 }}>
          <button
            className="logout-button"
            onClick={handleLogout}
          >
            Sair
          </button>
        </div>
      </div>
    </aside>
  );
}

export default Sidebar;
