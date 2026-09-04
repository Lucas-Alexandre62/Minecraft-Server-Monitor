import { useEffect } from "react";

function Toast({ message, type = "success", onClose }) {
  useEffect(() => {
    const timer = setTimeout(() => {
      onClose();
    }, 4000);

    return () => clearTimeout(timer);
  }, [onClose]);

  return (
    <div className={`toast ${type}`}>
      <div className="toast-content">
        <strong>
          {type === "success"
            ? "Sucesso"
            : type === "error"
              ? "Erro"
              : "Informação"}
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
