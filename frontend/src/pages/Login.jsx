import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { API_URL, setToken } from "../api";

function Login() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");

  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const navigate = useNavigate();

  async function handleSubmit(event) {
    event.preventDefault();

    setError("");
    setLoading(true);

    try {
      const response = await fetch(`${API_URL}/auth/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password }),
      });

      const data = await response.json();

      if (!response.ok) {
        throw new Error(
          "Usuário ou senha inválidos."
        );
      }

      setToken(data.token);

      navigate("/", { replace: true });
    } catch (error) {
      console.error(error);
      setError(error.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="login-page">
      <div className="login-card">
        <div className="login-header">
          <div className="sidebar-logo-icon">
            <svg viewBox="0 0 16 16" width="22" height="22" fill="none">
              <rect x="2" y="1" width="3" height="12" rx="0.5" fill="currentColor" opacity="0.85"/>
              <rect x="5" y="1" width="3" height="12" rx="0.5" fill="currentColor"/>
              <rect x="8" y="2" width="6" height="2" rx="0.5" fill="currentColor" opacity="0.7"/>
              <rect x="11" y="1" width="3" height="6" rx="0.5" fill="currentColor" opacity="0.6"/>
            </svg>
          </div>

          <h1>Minecraft Monitor</h1>

          <p>
            Entre para acessar o dashboard
          </p>
        </div>

        <form onSubmit={handleSubmit}>
          <div className="form-field">
            <label htmlFor="username">
              Usuário
            </label>

            <input
              id="username"
              type="text"
              value={username}
              onChange={(event) =>
                setUsername(event.target.value)
              }
              autoComplete="username"
              required
            />
          </div>

          <div className="form-field">
            <label htmlFor="password">
              Senha
            </label>

            <input
              id="password"
              type="password"
              value={password}
              onChange={(event) =>
                setPassword(event.target.value)
              }
              autoComplete="current-password"
              required
            />
          </div>

          {error && (
            <div className="form-error">
              {error}
            </div>
          )}

          <button
            type="submit"
            className="primary-button login-button"
            disabled={loading}
          >
            {loading ? "Entrando..." : "Entrar"}
          </button>
        </form>
      </div>
    </div>
  );
}

export default Login;
