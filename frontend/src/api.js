const API_URL = "http://localhost:8080/api";

export function getToken() {
  return sessionStorage.getItem("token");
}

export function setToken(token) {
  sessionStorage.setItem("token", token);
}

export function clearToken() {
  sessionStorage.removeItem("token");
}

export function isAuthenticated() {
  return Boolean(getToken());
}

export async function apiFetch(endpoint, options = {}) {
  const token = getToken();

  const headers = {
    ...(options.headers || {}),
  };

  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }

  const response = await fetch(`${API_URL}${endpoint}`, {
    ...options,
    headers,
  });

  if (response.status === 401) {
    clearToken();

    window.location.href = "/login";

    throw new Error("Sessão expirada.");
  }

  return response;
}
