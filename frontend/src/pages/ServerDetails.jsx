import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import MetricCard from "../components/MetricCard";
import ChartCard from "../components/ChartCard";
import EventList from "../components/EventList";
import { apiFetch } from "../api";

function ServerDetails() {
  const { serverId } = useParams();
  const navigate = useNavigate();

  const [server, setServer] = useState(null);
  const [status, setStatus] = useState(null);
  const [statistics, setStatistics] = useState(null);
  const [history, setHistory] = useState(null);
  const [historyPage, setHistoryPage] = useState(0);
  const [events, setEvents] = useState(null);
  const [eventPage, setEventPage] = useState(0);
  const [eventFilter, setEventFilter] = useState("");

  const [metrics, setMetrics] = useState(null);
  const [metricsPeriod, setMetricsPeriod] = useState("1h");

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  async function loadHistory(page) {
    try {
      const response = await apiFetch(
        `/servers/${serverId}/history?page=${page}&size=10`
      );

      if (!response.ok) {
        throw new Error("Erro ao carregar histórico.");
      }

      const data = await response.json();
      setHistory(data);
    } catch (error) {
      console.error(error);
      setHistory(null);
    }
  }

  async function loadEvents(page, filter) {
    try {
      let url = `/servers/${serverId}/events?page=${page}&size=10`;

      if (filter) {
        url += `&type=${filter}`;
      }

      const response = await apiFetch(url);

      if (!response.ok) {
        throw new Error("Erro ao carregar eventos.");
      }

      const data = await response.json();
      setEvents(data);
    } catch (error) {
      console.error(error);
      setEvents(null);
    }
  }

  async function loadDetails() {
    try {
      setError("");

      const [
        serverResponse,
        statusResponse,
        statisticsResponse,
      ] = await Promise.all([
        apiFetch(`/servers/${serverId}`),
        apiFetch(`/servers/${serverId}/status`),
        apiFetch(
          `/servers/${serverId}/statistics?hours=24`
        ),
      ]);

      if (!serverResponse.ok) {
        throw new Error("Servidor não encontrado.");
      }

      if (
        !statusResponse.ok ||
        !statisticsResponse.ok
      ) {
        throw new Error(
          "Erro ao carregar informações do servidor."
        );
      }

      const [
        serverData,
        statusData,
        statisticsData,
      ] = await Promise.all([
        serverResponse.json(),
        statusResponse.json(),
        statisticsResponse.json(),
      ]);

      setServer(serverData);
      setStatus(statusData);
      setStatistics(statisticsData);

      await loadHistory(0);
      await loadEvents(0, "");
    } catch (error) {
      console.error(error);
      setError(error.message);
    } finally {
      setLoading(false);
    }
  }

  async function loadMetrics() {
    try {
      let endpoint;

      if (metricsPeriod === "10m") {
        endpoint = `/servers/${serverId}/metrics?minutes=10`;
      } else {
        const hours = metricsPeriod.replace("h", "");

        endpoint = `/servers/${serverId}/metrics?hours=${hours}`;
      }

      const response = await apiFetch(endpoint);

      if (!response.ok) {
        throw new Error("Erro ao carregar métricas.");
      }

      const data = await response.json();

      setMetrics(data);
    } catch (error) {
      console.error(error);
      setMetrics(null);
    }
  }

  useEffect(() => {
    loadDetails();
  }, [serverId]);

  useEffect(() => {
    loadEvents(eventPage, eventFilter);
  }, [serverId, eventPage, eventFilter]);

  useEffect(() => {
    loadHistory(historyPage);
  }, [serverId, historyPage]);

  useEffect(() => {
    loadMetrics();

    const interval = setInterval(() => {
      loadMetrics();
    }, 30000);

    return () => clearInterval(interval);
  }, [serverId, metricsPeriod]);

  useEffect(() => {
    const interval = setInterval(async () => {
      try {
        const response = await apiFetch(
          `/servers/${serverId}/status`
        );

        if (!response.ok) {
          return;
        }

        const data = await response.json();

        setStatus(data);
      } catch (error) {
        console.error(error);
      }
    }, 30000);

    return () => clearInterval(interval);
  }, [serverId]);

  const chartData =
    metrics?.data?.map((point) => ({
      time: new Date(point.checkedAt).toLocaleTimeString(
        [],
        {
          hour: "2-digit",
          minute: "2-digit",
        }
      ),
      players: point.online ? point.players : null,
      latency: point.online ? point.latency : null,
    })) ?? [];

  if (loading) {
    return (
      <div className="app">
        <main className="container">
          <div className="message">
            Carregando servidor...
          </div>
        </main>
      </div>
    );
  }

  if (error || !server) {
    return (
      <div className="app">
        <main className="container">
          <div className="message error">
            {error || "Servidor não encontrado."}
          </div>

          <button
            className="refresh-button"
            onClick={() => navigate("/servers")}
          >
            Voltar
          </button>
        </main>
      </div>
    );
  }

  return (
    <div className="app">
      <header className="header">
        <div>
          <button
            className="back-button"
            onClick={() => navigate("/servers")}
          >
            <span className="back-arrow">&larr;</span> Servidores
          </button>

          <h1>{server.name}</h1>

          <div className="header-address">
            <span>{server.host}</span>
            <span className="header-port">:{server.port}</span>
          </div>
        </div>

        <div className="header-actions">
          <span
            className={
              status?.online
                ? "status online"
                : "status offline"
            }
          >
            {status?.online ? "ONLINE" : "OFFLINE"}
          </span>

          <button
            className="refresh-button"
            onClick={loadDetails}
          >
            Atualizar
          </button>
        </div>
      </header>

      <main className="container">
        <section className="details-top">
          <div className="current-status">
            <div className="current-status-identity">
              <span
                className={
                  status?.online
                    ? "status-dot online"
                    : "status-dot offline"
                }
              />
              <strong>
                {status?.online
                  ? "Servidor funcionando normalmente"
                  : "Servidor indisponível"}
              </strong>
            </div>

            {status?.version && (
              <div className="current-status-version">
                <span className="version-label">VERSÃO</span>
                <span className="version-value">{status.version}</span>
              </div>
            )}
          </div>

          <div className="details-metrics">
            <MetricCard
              label="Jogadores"
              value={
                status
                  ? `${status.playersOnline}/${status.maxPlayers}`
                  : "--"
              }
            />

            <MetricCard
              label="Latência"
              value={
                status?.online
                  ? `${status.latency} ms`
                  : "--"
              }
            />

            <MetricCard
              label="Versão"
              value={status?.version ?? "--"}
            />

            <MetricCard
              label="Uptime 24h"
              value={
                statistics
                  ? `${statistics.uptimePercentage}%`
                  : "--"
              }
            />
          </div>
        </section>

        <div className="section-divider" />

        {statistics && (
          <section className="dashboard-section">
            <div className="section-header">
              <h2>Estatísticas — últimas 24h</h2>
            </div>

            <div className="statistics-grid">
              <MetricCard
                label="Verificações"
                value={statistics.totalChecks}
              />

              <MetricCard
                label="Online"
                value={statistics.onlineChecks}
              />

              <MetricCard
                label="Offline"
                value={statistics.offlineChecks}
              />

              <MetricCard
                label="Média de jogadores"
                value={statistics.averagePlayers?.toFixed(1) ?? "--"}
              />

              <MetricCard
                label="Pico de jogadores"
                value={statistics.peakPlayers}
              />

              <MetricCard
                label="Latência média"
                value={statistics.averageLatency?.toFixed(0) != null ? `${statistics.averageLatency.toFixed(0)} ms` : "--"}
              />
            </div>
          </section>
        )}

        {statistics && <div className="section-divider" />}

        <section className="dashboard-section">
          <div className="section-header">
            <div>
              <h2>Monitoramento</h2>

              <span className="section-subtitle">
                {metrics?.data?.length ?? 0} pontos coletados
              </span>
            </div>

            <select
              className="period-select"
              value={metricsPeriod}
              onChange={(event) =>
                setMetricsPeriod(event.target.value)
              }
            >
              <option value="10m">
                Últimos 10 minutos
              </option>

              <option value="1h">
                Última 1 hora
              </option>

              <option value="6h">
                Últimas 6 horas
              </option>

              <option value="24h">
                Últimas 24 horas
              </option>
            </select>
          </div>

          <div className="charts-grid">
            <ChartCard
              title="Latência"
              data={chartData}
              dataKey="latency"
              name="Latência (ms)"
            />

            <ChartCard
              title="Jogadores"
              data={chartData}
              dataKey="players"
              name="Jogadores"
              allowDecimals={false}
            />
          </div>
        </section>

        <div className="section-divider" />

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
            <div>
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

              {history.totalPages > 1 && (
                <div className="events-pagination">
                  <button
                    className="pagination-button"
                    disabled={historyPage === 0}
                    onClick={() => setHistoryPage(historyPage - 1)}
                  >
                    ← Anterior
                  </button>

                  <span className="pagination-info">
                    Página {historyPage + 1} de {history.totalPages}
                    {history.totalElements > 0 && (
                      <> — {history.totalElements} registro{history.totalElements !== 1 ? "s" : ""}</>
                    )}
                  </span>

                  <button
                    className="pagination-button"
                    disabled={historyPage >= history.totalPages - 1}
                    onClick={() => setHistoryPage(historyPage + 1)}
                  >
                    Próxima →
                  </button>
                </div>
              )}
            </div>
          )}
        </section>

        <div className="section-divider" />

        <section className="dashboard-section">
          <div className="section-header">
            <h2>Eventos</h2>
          </div>

          <EventList
            events={events?.content ?? []}
            page={events?.number ?? 0}
            totalPages={events?.totalPages ?? 0}
            totalElements={events?.totalElements ?? 0}
            onPageChange={setEventPage}
            filter={eventFilter}
            onFilterChange={(value) => {
              setEventFilter(value);
              setEventPage(0);
            }}
          />
        </section>
      </main>
    </div>
  );
}

export default ServerDetails;
