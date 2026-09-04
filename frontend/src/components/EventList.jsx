function EventList({
  events,
  page,
  totalPages,
  totalElements,
  onPageChange,
  filter,
  onFilterChange,
}) {
  if (!events?.length) {
    return (
      <div className="message">
        Nenhum evento registrado.
      </div>
    );
  }

  function getEventLabel(type) {
    switch (type) {
      case "SERVER_UP":
        return "Servidor online";
      case "SERVER_DOWN":
        return "Servidor offline";
      case "HIGH_LATENCY":
        return "Latência elevada";
      default:
        return type;
    }
  }

  function getIndicatorClass(type) {
    switch (type) {
      case "SERVER_UP":
        return "event-indicator up";
      case "SERVER_DOWN":
        return "event-indicator down";
      case "HIGH_LATENCY":
        return "event-indicator latency";
      default:
        return "event-indicator";
    }
  }

  return (
    <div>
      <div className="events-filter">
        <button
          className={`filter-button ${filter === "" ? "active" : ""}`}
          onClick={() => onFilterChange("")}
        >
          Todos
        </button>

        <button
          className={`filter-button ${filter === "SERVER_UP" ? "active" : ""}`}
          onClick={() => onFilterChange("SERVER_UP")}
        >
          Online
        </button>

        <button
          className={`filter-button ${filter === "SERVER_DOWN" ? "active" : ""}`}
          onClick={() => onFilterChange("SERVER_DOWN")}
        >
          Offline
        </button>

        <button
          className={`filter-button ${filter === "HIGH_LATENCY" ? "active" : ""}`}
          onClick={() => onFilterChange("HIGH_LATENCY")}
        >
          Latência
        </button>
      </div>

      <div className="events-list">
        {events.map((event) => (
          <div
            className="event-item"
            key={event.id}
          >
            <span
              className={getIndicatorClass(event.type)}
            />

            <div>
              <strong>
                {getEventLabel(event.type)}
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

      {totalPages > 1 && (
        <div className="events-pagination">
          <button
            className="pagination-button"
            disabled={page === 0}
            onClick={() => onPageChange(page - 1)}
          >
            ← Anterior
          </button>

          <span className="pagination-info">
            Página {page + 1} de {totalPages}
            {totalElements > 0 && (
              <> — {totalElements} evento{totalElements !== 1 ? "s" : ""}</>
            )}
          </span>

          <button
            className="pagination-button"
            disabled={page >= totalPages - 1}
            onClick={() => onPageChange(page + 1)}
          >
            Próxima →
          </button>
        </div>
      )}
    </div>
  );
}

export default EventList;
