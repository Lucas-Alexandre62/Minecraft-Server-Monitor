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
      </div>

      <div className="events-list">
        {events.map((event) => {
          const isUp = event.type === "SERVER_UP";

          return (
            <div
              className="event-item"
              key={event.id}
            >
              <span
                className={
                  isUp
                    ? "event-indicator up"
                    : "event-indicator down"
                }
              />

              <div>
                <strong>
                  {isUp
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
          );
        })}
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
