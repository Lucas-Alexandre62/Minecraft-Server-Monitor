import { useEffect, useState } from "react";
import { apiFetch } from "../api";

const emptyForm = {
  name: "",
  host: "",
  port: "",
};

function ServerForm({
  server,
  onSuccess,
  onCancel,
}) {
  const [form, setForm] = useState(emptyForm);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const isEditing = Boolean(server);

  useEffect(() => {
    if (server) {
      setForm({
        name: server.name,
        host: server.host,
        port: server.port,
      });
    } else {
      setForm(emptyForm);
    }

    setError("");
  }, [server]);

  function handleChange(event) {
    const { name, value } = event.target;

    setForm((current) => ({
      ...current,
      [name]: value,
    }));
  }

  async function handleSubmit(event) {
    event.preventDefault();

    setError("");
    setLoading(true);

    try {
      const payload = {
        name: form.name.trim(),
        host: form.host.trim(),
        port: Number(form.port),
      };


      const endpoint = isEditing
        ? `/servers/${server.id}`
        : "/servers";

      const response = await apiFetch(endpoint, {
        method: isEditing ? "PUT" : "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(payload),
      });

      const data = await response.json();

      if (!response.ok) {
        throw new Error(
          data.message || "Não foi possível salvar o servidor."
        );
      }

      onSuccess(data);
    } catch (error) {
      console.error(error);
      setError(error.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="form-overlay">
      <div className="server-form-container">
        <div className="form-header">
          <div>
            <h2>
              {isEditing
                ? "Editar servidor"
                : "Adicionar servidor"}
            </h2>

            <p>
              {isEditing
                ? "Atualize os dados do servidor."
                : "Cadastre um novo servidor Minecraft."}
            </p>
          </div>

          <button
            className="close-button"
            onClick={onCancel}
            type="button"
          >
            ×
          </button>
        </div>

        <form onSubmit={handleSubmit}>
          <div className="form-field">
            <label htmlFor="name">
              Nome
            </label>

            <input
              id="name"
              name="name"
              type="text"
              value={form.name}
              onChange={handleChange}
              placeholder="Ex.: Meu Survival"
              required
            />
          </div>

          <div className="form-field">
            <label htmlFor="host">
              Host
            </label>

            <input
              id="host"
              name="host"
              type="text"
              value={form.host}
              onChange={handleChange}
              placeholder="Ex.: play.example.com"
              required
            />
          </div>

          <div className="form-field">
            <label htmlFor="port">
              Porta
            </label>

            <input
              id="port"
              name="port"
              type="number"
              min="1"
              max="65535"
              value={form.port}
              onChange={handleChange}
              placeholder="25565"
              required
            />
          </div>

          {error && (
            <div className="form-error">
              {error}
            </div>
          )}

          <div className="form-actions">
            <button
              type="button"
              className="secondary-button"
              onClick={onCancel}
              disabled={loading}
            >
              Cancelar
            </button>

            <button
              type="submit"
              className="primary-button"
              disabled={loading}
            >
              {loading
                ? "Salvando..."
                : isEditing
                  ? "Salvar alterações"
                  : "Adicionar servidor"}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default ServerForm;
