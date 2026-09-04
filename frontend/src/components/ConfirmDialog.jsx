function ConfirmDialog({
  title,
  message,
  onConfirm,
  onCancel,
  loading = false,
}) {
  return (
    <div className="form-overlay">
      <div className="confirm-dialog">
        <h2>{title}</h2>

        <p>{message}</p>

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
            type="button"
            className="danger-button"
            onClick={onConfirm}
            disabled={loading}
          >
            {loading ? "Excluindo..." : "Excluir"}
          </button>
        </div>
      </div>
    </div>
  );
}

export default ConfirmDialog;
