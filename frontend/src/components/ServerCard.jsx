function ServerCard({ server, status, onDetails }) {
  return (
    <article className="server-card">
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
        onClick={onDetails}
      >
        Ver detalhes
      </button>
    </article>
  );
}

export default ServerCard;
