import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import ServerCard from "../components/ServerCard";
import { apiFetch } from "../api";

function Dashboard() {
  const [servers, setServers] = useState([]);
  const [statuses, setStatuses] = useState({});
  const [aggregateStats, setAggregateStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const navigate = useNavigate();

  async function loadServers() {
    try {
      setError("");

      const [serversResponse, statsResponse] = await Promise.all([
        apiFetch(`/servers`),
        apiFetch(`/servers/statistics?hours=24`),
      ]);

      if (!serversResponse.ok) {
        throw new Error("Não foi possível carregar os servidores.");
      }

      const data = await serversResponse.json();
      setServers(data);

      if (statsResponse.ok) {
        const statsData = await statsResponse.json();
        setAggregateStats(statsData);
      }

      await loadStatuses(data);
    } catch (error) {
      console.error(error);
      setError("Não foi possível conectar à API.");
    } finally {
      setLoading(false);
    }
  }

  async function loadStatuses(serverList) {
    const results = await Promise.all(
      serverList.map(async (server) => {
        try {
          const response = await apiFetch(
            `/servers/${server.id}/status`
          );

          if (!response.ok) {
            throw new Error("Erro ao consultar status.");
          }

          const status = await response.json();

          return {
            id: server.id,
            status,
          };
        } catch (error) {
          console.error(
            `Erro ao consultar servidor ${server.id}:`,
            error
          );

          return {
            id: server.id,
            status: {
              online: false,
              playersOnline: 0,
              maxPlayers: 0,
              latency: 0,
              version: null,
            },
          };
        }
      })
    );

    const statusMap = {};

    results.forEach((result) => {
      statusMap[result.id] = result.status;
    });

    setStatuses(statusMap);
  }

  useEffect(() => {
    loadServers();

    const interval = setInterval(() => {
      loadServers();
    }, 10000);

    return () => clearInterval(interval);
  }, []);

  const onlineServers = Object.values(statuses).filter(
    (status) => status?.online
  ).length;

  const offlineServers =
    servers.length - onlineServers;

  const totalPlayers = Object.values(statuses).reduce(
    (total, status) =>
      total + (status?.online ? status.playersOnline : 0),
    0
  );

  const onlineStatuses = Object.values(statuses).filter(
    (status) => status?.online
  );

  const averageLatency =
    onlineStatuses.length === 0
      ? 0
      : Math.round(
          onlineStatuses.reduce(
            (total, status) => total + status.latency,
            0
          ) / onlineStatuses.length
        );

  return (
    <div className="app">
      <header className="header">
        <div>
          <h1>Minecraft Monitor</h1>
          <p>Monitoramento de servidores Minecraft</p>
        </div>

        <button
          className="refresh-button"
          onClick={loadServers}
        >
          Atualizar
        </button>
      </header>

      <main className="container">
        <section className="overview">
          <div className="overview-card">
            <span className="overview-label">
              Servidores
            </span>

            <strong>{servers.length}</strong>

            <span className="overview-description">
              cadastrados
            </span>
          </div>

          <div className="overview-card">
            <span className="overview-label">
              Online
            </span>

            <strong className="online-count">
              {onlineServers}
            </strong>

            <span className="overview-description">
              {offlineServers} offline
            </span>
          </div>

          <div className="overview-card">
            <span className="overview-label">
              Jogadores
            </span>

            <strong>{totalPlayers}</strong>

            <span className="overview-description">
              online agora
            </span>
          </div>

          <div className="overview-card">
            <span className="overview-label">
              Latência média
            </span>

            <strong>
              {averageLatency > 0
                ? `${averageLatency} ms`
                : "--"}
            </strong>

            <span className="overview-description">
              servidores online
            </span>
          </div>

          <div className="overview-card">
            <span className="overview-label">
              Disponibilidade
            </span>

            <strong>
              {aggregateStats
                ? `${aggregateStats.uptimePercentage.toFixed(1)}%`
                : "--"}
            </strong>

            <span className="overview-description">
              últimas 24h
            </span>
          </div>
        </section>

        <section className="servers-section">
          <div className="section-header">
            <h2>Servidores</h2>

            <span>
              {servers.length} cadastrados
            </span>
          </div>

          {loading && (
            <div className="message">
              Carregando servidores...
            </div>
          )}

          {error && (
            <div className="message error">
              {error}
            </div>
          )}

          {!loading &&
            !error &&
            servers.length === 0 && (
              <div className="message">
                Nenhum servidor cadastrado.
              </div>
            )}

          <div className="servers-grid">
            {servers.map((server) => (
              <ServerCard
                key={server.id}
                server={server}
                status={statuses[server.id]}
                onDetails={() =>
                  navigate(`/servers/${server.id}`)
                }
              />
            ))}
          </div>
        </section>
      </main>
    </div>
  );
}

export default Dashboard;
