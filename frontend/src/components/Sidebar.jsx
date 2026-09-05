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
        <div className="sidebar-logo-icon pixel-art-icon">
          <div className="px-grass-top"></div>
          <div className="px-dirt"></div>
        </div>

        <div>
          <strong>Minecraft</strong>
          <span>Monitor</span>
        </div>
      </div>

      <div className="pixel-separator" />

      <nav className="sidebar-nav">
        <NavLink
          to="/"
          end
          className={({ isActive }) =>
            isActive ? "nav-item active" : "nav-item"
          }
        >
          <span className="nav-icon">
            <svg viewBox="0 0 16 16" fill="none" stroke="currentColor" strokeWidth="1.5">
              <rect x="2" y="2" width="5" height="5" rx="1"/>
              <rect x="9" y="2" width="5" height="5" rx="1"/>
              <rect x="2" y="9" width="5" height="5" rx="1"/>
              <rect x="9" y="9" width="5" height="5" rx="1"/>
            </svg>
          </span>
          Dashboard
        </NavLink>

        <NavLink
          to="/servers"
          className={({ isActive }) =>
            isActive ? "nav-item active" : "nav-item"
          }
        >
          <span className="nav-icon">
            <svg viewBox="0 0 16 16" fill="none" stroke="currentColor" strokeWidth="1.5">
              <rect x="3" y="2" width="10" height="4" rx="1"/>
              <circle cx="5.5" cy="4" r="0.7" fill="currentColor"/>
              <rect x="3" y="10" width="10" height="4" rx="1"/>
              <circle cx="5.5" cy="12" r="0.7" fill="currentColor"/>
            </svg>
          </span>
          Servidores
        </NavLink>

        <NavLink
          to="/alerts"
          className={({ isActive }) =>
            isActive ? "nav-item active" : "nav-item"
          }
        >
          <span className="nav-icon">
            <svg viewBox="0 0 16 16" fill="none" stroke="currentColor" strokeWidth="1.5">
              <path d="M4 6.5C4 4.01 5.79 2 8 2s4 2.01 4 4.5V10l1.5 1.5H2.5L4 10V6.5z"/>
              <path d="M6.5 12.5a1.5 1.5 0 003 0"/>
            </svg>
          </span>
          Alertas
        </NavLink>
      </nav>

      <div className="sidebar-footer">
        <div className="sidebar-system-info">
          <span>SISTEMA</span>
          <small>v0.1.0 &middot; Spring Boot</small>
        </div>
        <div className="sidebar-footer-actions">
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
