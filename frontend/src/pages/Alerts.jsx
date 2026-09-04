import { useEffect, useState } from "react";
import { apiFetch } from "../api";

const CHANNEL_LABELS = {
  LOG: "Log do sistema",
  WEBHOOK: "Webhook",
  EMAIL: "E-mail",
};

function Alerts() {
  const [servers, setServers] = useState([]);
  const [selectedServer, setSelectedServer] = useState("");
  const [configs, setConfigs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(null);

  async function loadServers() {
    try {
      const response = await apiFetch("/servers");

      if (!response.ok) {
        throw new Error("Erro ao carregar servidores.");
      }

      const data = await response.json();
      setServers(data);

      if (data.length > 0 && !selectedServer) {
        setSelectedServer(String(data[0].id));
      }
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
    }
  }

  async function loadConfigs(serverId) {
    if (!serverId) {
      setConfigs([]);
      return;
    }

    try {
      const response = await apiFetch(
        `/servers/${serverId}/alerts`
      );

      if (!response.ok) {
        throw new Error("Erro ao carregar configurações.");
      }

      const data = await response.json();
      setConfigs(data);
    } catch (error) {
      console.error(error);
      setConfigs([]);
    }
  }

  async function toggleChannel(channel, enabled) {
    if (!selectedServer) {
      return;
    }

    try {
      setSaving(channel);

      const response = await apiFetch(
        `/servers/${selectedServer}/alerts`,
        {
          method: "PUT",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ channel, enabled }),
        }
      );

      if (!response.ok) {
        throw new Error("Erro ao salvar configuração.");
      }

      const updated = await response.json();

      setConfigs((current) => {
        const index = current.findIndex(
          (c) => c.channel === channel
        );

        if (index >= 0) {
          const next = [...current];
          next[index] = updated;
          return next;
        }

        return [...current, updated];
      });
    } catch (error) {
      console.error(error);
    } finally {
      setSaving(null);
    }
  }

  useEffect(() => {
    loadServers();
  }, []);

  useEffect(() => {
    loadConfigs(selectedServer);
  }, [selectedServer]);

  if (loading) {
    return (
      <div className="page">
        <div className="message">Carregando...</div>
      </div>
    );
  }

  return (
    <div className="page">
      <header className="page-header">
        <div>
          <h1>Alertas</h1>
          <p>Configure os canais de alerta por servidor.</p>
        </div>
      </header>

      {servers.length === 0 ? (
        <div className="message">
          Nenhum servidor cadastrado.
        </div>
      ) : (
        <>
          <div className="alerts-server-select">
            <label>Servidor:</label>
            <select
              className="period-select"
              value={selectedServer}
              onChange={(e) => setSelectedServer(e.target.value)}
            >
              {servers.map((server) => (
                <option key={server.id} value={server.id}>
                  {server.name}
                </option>
              ))}
            </select>
          </div>

          <div className="alerts-config-list">
            {Object.entries(CHANNEL_LABELS).map(
              ([channel, label]) => {
                const config = configs.find(
                  (c) => c.channel === channel
                );
                const enabled = config?.enabled ?? false;
                const isSaving = saving === channel;

                return (
                  <div
                    className="alerts-config-item"
                    key={channel}
                  >
                    <div>
                      <strong>{label}</strong>
                      <span>{channel}</span>
                    </div>

                    <button
                      className={`toggle-button ${enabled ? "active" : ""}`}
                      disabled={isSaving}
                      onClick={() =>
                        toggleChannel(channel, !enabled)
                      }
                    >
                      {isSaving
                        ? "Salvando..."
                        : enabled
                          ? "Ativado"
                          : "Desativado"}
                    </button>
                  </div>
                );
              }
            )}
          </div>
        </>
      )}
    </div>
  );
}

export default Alerts;
