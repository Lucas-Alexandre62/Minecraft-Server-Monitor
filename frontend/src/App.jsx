import { useEffect, useState } from "react";
import "./App.css";
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
} from "recharts";

const API_URL = "http://localhost:8080/api";

function App() {
  const [servers, setServers] = useState([]);
  const [statuses, setStatuses] = useState({});
  const [selectedServer, setSelectedServer] = useState(null);

  const [details, setDetails] = useState({
    status: null,
    statistics: null,
    history: null,
    events: null,
  });

  const [loading, setLoading] = useState(true);
  const [detailsLoading, setDetailsLoading] = useState(false);
  const [error, setError] = useState("");

  async function loadServers() {
    try {
      setError("");

      const response = await fetch(`${API_URL}/servers`);

      if (!response.ok) {
        throw new Error("Não foi possível carregar os servidores.");
      }

      const data = await response.json();

      setServers(data);

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
          const response = await fetch(
            `${API_URL}/servers/${server.id}/status`
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

  async function loadServerDetails(server) {
    try {
      setDetailsLoading(true);

      const [statusResponse, statisticsResponse, historyResponse, eventsResponse] =
        await Promise.all([
          fetch(`${API_URL}/servers/${server.id}/status`),
          fetch(`${API_URL}/servers/${server.id}/statistics?hours=24`),
          fetch(`${API_URL}/servers/${server.id}/history?page=0&size=20`),
          fetch(`${API_URL}/servers/${server.id}/events?page=0&size=20`),
        ]);

      if (
        !statusResponse.ok ||
        !statisticsResponse.ok ||
        !historyResponse.ok ||
        !eventsResponse.ok
      ) {
        throw new Error("Erro ao carregar detalhes.");
      }

      const [status, statistics, history, events] =
        await Promise.all([
          statusResponse.json(),
          statisticsResponse.json(),
          historyResponse.json(),
          eventsResponse.json(),
        ]);

      setDetails({
        status,
        statistics,
        history,
        events,
      });

      setSelectedServer(server);
    } catch (error) {
      console.error(error);
      setError("Não foi possível carregar os detalhes do servidor.");
    } finally {
      setDetailsLoading(false);
    }
  }

  function closeDetails() {
    setSelectedServer(null);

    setDetails({
      status: null,
      statistics: null,
      history: null,
      events: null,
    });
  }

  useEffect(() => {
    loadServers();

    const interval = setInterval(() => {
      loadServers();
    }, 10000);

    return () => clearInterval(interval);
  }, []);

  const onlineServers = Object.values(statuses).filter(
    (status) => status.online
  ).length;

  if (selectedServer) {
    return (
      <ServerDetails
        server={selectedServer}
        details={details}
        loading={detailsLoading}
        onBack={closeDetails}
        onRefresh={() => loadServerDetails(selectedServer)}
      />
    );
  }

  return (
    <div className="app">
      <header className="header">
        <div>
          <h1>Minecraft Monitor</h1>
          <p>Monitoramento de servidores Minecraft</p>
        </div>

        <button className="refresh-button" onClick={loadServers}>
          Atualizar
        </button>
      </header>

      <main className="container">
        <section className="overview">
          <div className="overview-card">
            <span className="overview-label">Servidores</span>
            <strong>{servers.length}</strong>
          </div>

          <div className="overview-card">
            <span className="overview-label">Online</span>
            <strong className="online-count">
              {onlineServers}
            </strong>
          </div>

          <div className="overview-card">
            <span className="overview-label">Atualização</span>
            <strong>10s</strong>
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
            {servers.map((server) => {
              const status = statuses[server.id];

              return (
                <article
                  className="server-card"
                  key={server.id}
                >
                  <div className="server-card-header">
                    <div>
                      <h3>{server.name}</h3>

                      <span className="server-id">
                        ID #{server.id}
                      </span>
                    </div>

                    {!status && (
                      <span className="status unknown">
                        VERIFICANDO
                      </span>
                    )}

                    {status?.online && (
                      <span className="status online">
                        ONLINE
                      </span>
                    )}

                    {status && !status.online && (
                      <span className="status offline">
                        OFFLINE
                      </span>
                    )}
                  </div>

                  <div className="server-address">
                    <span>{server.host}</span>
                    <span>:{server.port}</span>
                  </div>

                  <div className="server-info">
                    <div>
                      <span>Jogadores</span>
                      <strong>
                        {status
                          ? `${status.playersOnline}/${status.maxPlayers}`
                          : "--"}
                      </strong>
                    </div>

                    <div>
                      <span>Latência</span>
                      <strong>
                        {status?.online
                          ? `${status.latency} ms`
                          : "--"}
                      </strong>
                    </div>

                    <div>
                      <span>Versão</span>
                      <strong>
                        {status?.version ?? "--"}
                      </strong>
                    </div>
                  </div>

                  <button
                    className="details-button"
                    onClick={() => loadServerDetails(server)}
                  >
                    Ver detalhes
                  </button>
                </article>
              );
            })}
          </div>
        </section>
      </main>
    </div>
  );
}

function ServerDetails({
  server,
  details,
  loading,
  onBack,
  onRefresh,
}) {
  const status = details.status;
  const statistics = details.statistics;
  const history = details.history;
  const events = details.events;

  const chartData =
    history?.content?.map((entry) => ({
      time: new Date(entry.checkedAt).toLocaleTimeString([], {
        hour: "2-digit",
        minute: "2-digit",
      }),
      latency: entry.online ? entry.latency : null,
      players: entry.online ? entry.playersOnline : null,
    })) ?? [];

  return (
    <div className="app">
      <header className="header">
        <div>
          <button
            className="back-button"
            onClick={onBack}
          >
            ← Voltar
          </button>

          <h1>{server.name}</h1>

          <p>
            {server.host}:{server.port}
          </p>
        </div>

        <button
          className="refresh-button"
          onClick={onRefresh}
        >
          Atualizar
        </button>
      </header>

      <main className="container">
        {loading && (
          <div className="message">
            Carregando detalhes...
          </div>
        )}

        {!loading && status && (
          <>
            <section className="details-top">
              <div className="current-status">
                <span
                  className={
                    status.online
                      ? "status online"
                      : "status offline"
                  }
                >
                  {status.online
                    ? "ONLINE"
                    : "OFFLINE"}
                </span>

                <strong>
                  {status.online
                    ? "Servidor funcionando normalmente"
                    : "Servidor indisponível"}
                </strong>
              </div>

              <div className="details-metrics">
                <div className="metric-card">
                  <span>Jogadores</span>

                  <strong>
                    {status.playersOnline}/
                    {status.maxPlayers}
                  </strong>
                </div>

                <div className="metric-card">
                  <span>Latência</span>

                  <strong>
                    {status.online
                      ? `${status.latency} ms`
                      : "--"}
                  </strong>
                </div>

                <div className="metric-card">
                  <span>Versão</span>

                  <strong>
                    {status.version ?? "--"}
                  </strong>
                </div>

                <div className="metric-card">
                  <span>Uptime 24h</span>

                  <strong>
                    {statistics
                      ? `${statistics.uptimePercentage}%`
                      : "--"}
                  </strong>
                </div>
              </div>
            </section>

            {statistics && (
              <section className="dashboard-section">
                <div className="section-header">
                  <h2>Estatísticas — últimas 24h</h2>
                </div>

                <div className="statistics-grid">
                  <div className="metric-card">
                    <span>Verificações</span>
                    <strong>
                      {statistics.totalChecks}
                    </strong>
                  </div>

                  <div className="metric-card">
                    <span>Online</span>
                    <strong>
                      {statistics.onlineChecks}
                    </strong>
                  </div>

                  <div className="metric-card">
                    <span>Offline</span>
                    <strong>
                      {statistics.offlineChecks}
                    </strong>
                  </div>

                  <div className="metric-card">
                    <span>Média de jogadores</span>
                    <strong>
                      {statistics.averagePlayers.toFixed(
                        1
                      )}
                    </strong>
                  </div>

                  <div className="metric-card">
                    <span>Pico de jogadores</span>
                    <strong>
                      {statistics.peakPlayers}
                    </strong>
                  </div>

                  <div className="metric-card">
                    <span>Latência média</span>
                    <strong>
                      {statistics.averageLatency.toFixed(
                        0
                      )}{" "}
                      ms
                    </strong>
                  </div>
                </div>
              </section>
            )}

            <section className="dashboard-section">
              <div className="section-header">
                <h2>Monitoramento</h2>
              </div>

              <div className="charts-grid">
                <div className="chart-card">
                  <h3>Latência</h3>

                  <ResponsiveContainer width="100%" height={280}>
                    <LineChart data={chartData}>
                      <CartesianGrid strokeDasharray="3 3" />

                      <XAxis dataKey="time" />

                      <YAxis />

                      <Tooltip />

                      <Line
                        type="monotone"
                        dataKey="latency"
                        name="Latência (ms)"
                        strokeWidth={2}
                        dot={false}
                      />
                    </LineChart>
                  </ResponsiveContainer>
                </div>

                <div className="chart-card">
                  <h3>Jogadores</h3>

                  <ResponsiveContainer width="100%" height={280}>
                    <LineChart data={chartData}>
                      <CartesianGrid strokeDasharray="3 3" />

                      <XAxis dataKey="time" />

                      <YAxis />

                      <Tooltip />

                      <Line
                        type="monotone"
                        dataKey="players"
                        name="Jogadores"
                        strokeWidth={2}
                        dot={false}
                      />
                    </LineChart>
                  </ResponsiveContainer>
                </div>
              </div>
            </section>

            <section className="dashboard-section">
              <div className="section-header">
                <h2>Histórico recente</h2>
              </div>

              {history?.content?.length === 0 && (
                <div className="message">
                  Nenhum registro encontrado.
                </div>
              )}

              {history?.content?.length > 0 && (
                <div className="table-container">
                  <table>
                    <thead>
                      <tr>
                        <th>Data</th>
                        <th>Status</th>
                        <th>Jogadores</th>
                        <th>Latência</th>
                        <th>Versão</th>
                      </tr>
                    </thead>

                    <tbody>
                      {history.content.map((entry) => (
                        <tr key={entry.id}>
                          <td>
                            {new Date(
                              entry.checkedAt
                            ).toLocaleString()}
                          </td>

                          <td>
                            <span
                              className={
                                entry.online
                                  ? "table-status online-text"
                                  : "table-status offline-text"
                              }
                            >
                              {entry.online
                                ? "ONLINE"
                                : "OFFLINE"}
                            </span>
                          </td>

                          <td>
                            {entry.playersOnline}/
                            {entry.maxPlayers}
                          </td>

                          <td>
                            {entry.online
                              ? `${entry.latency} ms`
                              : "--"}
                          </td>

                          <td>
                            {entry.version ?? "--"}
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              )}
            </section>

            <section className="dashboard-section">
              <div className="section-header">
                <h2>Eventos</h2>
              </div>

              {events?.content?.length === 0 && (
                <div className="message">
                  Nenhum evento registrado.
                </div>
              )}

              {events?.content?.length > 0 && (
                <div className="events-list">
                  {events.content.map((event) => (
                    <div
                      className="event-item"
                      key={event.id}
                    >
                      <span
                        className={
                          event.type === "SERVER_UP"
                            ? "event-indicator up"
                            : "event-indicator down"
                        }
                      />

                      <div>
                        <strong>
                          {event.type === "SERVER_UP"
                            ? "Servidor online"
                            : "Servidor offline"}
                        </strong>

                        <span>
                          {new Date(
                            event.createdAt
                          ).toLocaleString()}
                        </span>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </section>
          </>
        )}
      </main>
    </div>
  );
}

export default App;