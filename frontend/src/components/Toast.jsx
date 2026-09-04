function Toast({ message, type = "success", onClose }) {
  return (
    <div className={`toast ${type}`}>
      <div className="toast-content">
        <strong>
          {type === "success" ? "Sucesso" : "Erro"}
        </strong>

        <span>{message}</span>
      </div>

      <button onClick={onClose}>
        ×
      </button>
    </div>
  );
}

export default Toast;
