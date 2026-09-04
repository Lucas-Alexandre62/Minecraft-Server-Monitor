function EventList({ events }) {
  if (!events?.content?.length) {
    return (
      <div className="message">
        Nenhum evento registrado.
      </div>
    );
  }

  return (
    <div className="events-list">
      {events.content.map((event) => {
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
  );
}

export default EventList;
