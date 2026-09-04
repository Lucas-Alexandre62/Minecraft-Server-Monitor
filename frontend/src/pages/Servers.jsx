import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import ServerForm from "../components/ServerForm";
import Toast from "../components/Toast";
import ConfirmDialog from "../components/ConfirmDialog";
import { apiFetch } from "../api";

function Servers() {
  const [servers, setServers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const navigate = useNavigate();
  const [showForm, setShowForm] = useState(false);
  const [editingServer, setEditingServer] = useState(null);
  const [toast, setToast] = useState(null);
  const [serverToDelete, setServerToDelete] = useState(null);
  const [deleting, setDeleting] = useState(false);

  async function loadServers() {
    try {
      setError("");

      const response = await apiFetch(`/servers`);

      if (!response.ok) {
        throw new Error("Não foi possível carregar os servidores.");
      }

      const data = await response.json();

      setServers(data);
    } catch (error) {
      console.error(error);
      setError("Não foi possível conectar à API.");
    } finally {
      setLoading(false);
    }
  }

  async function deleteServer() {
    if (!serverToDelete) {
      return;
    }

    try {
      setDeleting(true);

      const response = await apiFetch(
        `/servers/${serverToDelete.id}`,
        {
          method: "DELETE",
        }
      );

      if (!response.ok) {
        let message = "Não foi possível excluir o servidor.";

        try {
          const data = await response.json();

          if (data.message) {
            message = data.message;
          }
        } catch {
          // resposta sem JSON
        }

        throw new Error(message);
      }

      setServers((currentServers) =>
        currentServers.filter((s) => s.id !== serverToDelete.id)
      );

      setToast({
        type: "success",
        message: `Servidor "${serverToDelete.name}" excluído com sucesso.`,
      });

      setServerToDelete(null);
    } catch (error) {
      console.error(error);

      setToast({
        type: "error",
        message: error.message,
      });
    } finally {
      setDeleting(false);
    }
  }

  function handleAdd() {
    setEditingServer(null);
    setShowForm(true);
  }

  function handleEdit(server) {
    setEditingServer(server);
    setShowForm(true);
  }

  function handleFormSuccess(server) {
    const wasEditing = Boolean(editingServer);

    setShowForm(false);
    setEditingServer(null);

    setToast({
      type: "success",
      message: wasEditing
        ? `Servidor "${server.name}" atualizado com sucesso.`
        : `Servidor "${server.name}" adicionado com sucesso.`,
    });

    loadServers();
  }

  function handleCancelForm() {
    setShowForm(false);
    setEditingServer(null);
  }

  useEffect(() => {
    loadServers();
  }, []);

  return (
    <div className="page">
      <header className="page-header">
        <div>
          <h1>Servidores</h1>
          <p>Gerencie os servidores cadastrados.</p>
        </div>

        <button className="primary-button" onClick={handleAdd}>
          + Adicionar servidor
        </button>
      </header>

      {loading && <div className="message">Carregando servidores...</div>}

      {error && <div className="message error">{error}</div>}

      {!loading && !error && (
        <div className="management-table">
          <div className="table-container">
            <table>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Nome</th>
                  <th>Host</th>
                  <th>Porta</th>
                  <th>Ações</th>
                </tr>
              </thead>

              <tbody>
                {servers.map((server) => (
                  <tr key={server.id}>
                    <td>#{server.id}</td>

                    <td>
                      <strong>{server.name}</strong>
                    </td>

                    <td>{server.host}</td>

                    <td>{server.port}</td>

                    <td>
                      <div className="action-buttons">
                        <button
                          className="secondary-button"
                          onClick={() => navigate(`/servers/${server.id}`)}
                        >
                          Detalhes
                        </button>

                        <button
                          className="secondary-button"
                          onClick={() => handleEdit(server)}
                        >
                          Editar
                        </button>

                        <button
                          className="danger-button"
                          onClick={() => setServerToDelete(server)}
                        >
                          Excluir
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}

                {servers.length === 0 && (
                  <tr>
                    <td colSpan="5" className="empty-cell">
                      Nenhum servidor cadastrado.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {showForm && (
        <ServerForm server={editingServer} onSuccess={handleFormSuccess} onCancel={handleCancelForm} />
      )}

      {serverToDelete && (
        <ConfirmDialog
          title="Excluir servidor?"
          message={`O servidor "${serverToDelete.name}" será removido do monitoramento.`}
          onConfirm={deleteServer}
          onCancel={() => setServerToDelete(null)}
          loading={deleting}
        />
      )}

      {toast && <Toast message={toast.message} type={toast.type} onClose={() => setToast(null)} />}
    </div>
  );
}

export default Servers;
