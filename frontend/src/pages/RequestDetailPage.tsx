import { useCallback, useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { changeRequestStatus, deleteRequest, getRequest, getRequestHistory } from '../api/requests';
import { PriorityBadge, StatusBadge } from '../components/ui/Badges';
import { ConfirmDialog } from '../components/ui/ConfirmDialog';
import { ErrorState } from '../components/ui/ErrorState';
import { PageLoader, Spinner } from '../components/ui/Spinner';
import { useToast } from '../contexts/ToastContext';
import type { RequestStatus, ServiceRequest, StatusHistoryEntry } from '../types';
import { getErrorMessage } from '../utils/erros';
import { formatDateTime } from '../utils/format';
import { STATUS_LABELS, STATUS_ORDER, nextStatus } from '../utils/labels';

const ADVANCE_LABELS: Partial<Record<RequestStatus, string>> = {
  IN_PROGRESS: 'Iniciar atendimento',
  COMPLETED: 'Concluir solicitação',
};

export function RequestDetailPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const toast = useToast();

  const [request, setRequest] = useState<ServiceRequest | null>(null);
  const [history, setHistory] = useState<StatusHistoryEntry[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [advancing, setAdvancing] = useState(false);
  const [confirmingDelete, setConfirmingDelete] = useState(false);
  const [deleting, setDeleting] = useState(false);

  const load = useCallback(
    async (signal?: AbortSignal) => {
      if (!id) return;
      setLoading(true);
      setError(null);
      try {
        const [loadedRequest, loadedHistory] = await Promise.all([
          getRequest(id, signal),
          getRequestHistory(id, signal),
        ]);
        setRequest(loadedRequest);
        setHistory(loadedHistory);
      } catch (loadError) {
        if (signal?.aborted) return;
        setError(getErrorMessage(loadError));
      } finally {
        if (!signal?.aborted) setLoading(false);
      }
    },
    [id],
  );

  useEffect(() => {
    const controller = new AbortController();
    void load(controller.signal);
    return () => controller.abort();
  }, [load]);

  const handleAdvance = async (target: RequestStatus) => {
    if (!request) return;
    setAdvancing(true);
    try {
      const updated = await changeRequestStatus(request.id, target);
      setRequest(updated);
      setHistory(await getRequestHistory(request.id));
      toast.success(`Status alterado para “${STATUS_LABELS[updated.status]}”.`);
    } catch (advanceError) {
      toast.error(getErrorMessage(advanceError));
    } finally {
      setAdvancing(false);
    }
  };

  const handleDelete = async () => {
    if (!request) return;
    setDeleting(true);
    try {
      await deleteRequest(request.id);
      toast.success('Solicitação excluída com sucesso.');
      navigate('/requests', { replace: true });
    } catch (deleteError) {
      toast.error(getErrorMessage(deleteError));
      setDeleting(false);
      setConfirmingDelete(false);
    }
  };

  if (loading) return <PageLoader message="Carregando solicitação..." />;
  if (error || !request) {
    return <ErrorState message={error ?? 'Solicitação não encontrada.'} onRetry={error ? () => void load() : undefined} />;
  }

  const upcoming = nextStatus(request.status);
  const currentIndex = STATUS_ORDER.indexOf(request.status);

  return (
    <div className="mx-auto max-w-3xl space-y-6">
      <Link to="/requests" className="text-sm text-slate-500 hover:text-slate-700">
        ← Voltar para solicitações
      </Link>

      <div className="card p-6">
        <div className="flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between">
          <div className="min-w-0">
            <h1 className="break-words text-2xl font-semibold text-slate-900">{request.title}</h1>
            <div className="mt-2 flex flex-wrap gap-2">
              <StatusBadge status={request.status} />
              <PriorityBadge priority={request.priority} />
            </div>
          </div>
          <div className="flex shrink-0 gap-2">
            <Link to={`/requests/${request.id}/edit`} className="btn-secondary">
              Editar
            </Link>
            <button type="button" className="btn-secondary text-red-700" onClick={() => setConfirmingDelete(true)}>
              Excluir
            </button>
          </div>
        </div>

        <p className="mt-5 whitespace-pre-wrap break-words text-sm leading-relaxed text-slate-700">
          {request.description}
        </p>

        <dl className="mt-6 grid grid-cols-1 gap-4 border-t border-slate-100 pt-5 text-sm sm:grid-cols-2">
          <div>
            <dt className="text-slate-500">Categoria</dt>
            <dd className="font-medium text-slate-800">{request.category}</dd>
          </div>
          <div>
            <dt className="text-slate-500">Solicitante</dt>
            <dd className="font-medium text-slate-800">{request.ownerName}</dd>
          </div>
          <div>
            <dt className="text-slate-500">Criada em</dt>
            <dd className="font-medium text-slate-800">{formatDateTime(request.createdAt)}</dd>
          </div>
          <div>
            <dt className="text-slate-500">Última atualização</dt>
            <dd className="font-medium text-slate-800">{formatDateTime(request.updatedAt)}</dd>
          </div>
        </dl>
      </div>

      <section className="card p-6" aria-labelledby="progress-title">
        <h2 id="progress-title" className="text-base font-semibold text-slate-900">
          Andamento
        </h2>

        <ol className="mt-4 flex items-center">
          {STATUS_ORDER.map((status, index) => {
            const reached = index <= currentIndex;
            return (
              <li key={status} className="flex flex-1 items-center last:flex-none">
                <div className="flex flex-col items-center gap-1">
                  <span
                    className={`flex h-8 w-8 items-center justify-center rounded-full text-xs font-semibold ${
                      reached ? 'bg-brand-700 text-white' : 'bg-slate-100 text-slate-500'
                    }`}
                    aria-current={index === currentIndex ? 'step' : undefined}
                  >
                    {index + 1}
                  </span>
                  <span className={`text-xs ${reached ? 'font-medium text-slate-800' : 'text-slate-500'}`}>
                    {STATUS_LABELS[status]}
                  </span>
                </div>
                {index < STATUS_ORDER.length - 1 && (
                  <span className={`mx-2 mb-5 h-0.5 flex-1 ${index < currentIndex ? 'bg-brand-700' : 'bg-slate-200'}`} />
                )}
              </li>
            );
          })}
        </ol>

        <div className="mt-6">
          {upcoming ? (
            <button type="button" className="btn-primary" disabled={advancing} onClick={() => void handleAdvance(upcoming)}>
              {advancing && <Spinner className="h-4 w-4" />}
              {ADVANCE_LABELS[upcoming] ?? `Avançar para ${STATUS_LABELS[upcoming]}`}
            </button>
          ) : (
            <p className="text-sm text-emerald-700">Esta solicitação foi concluída e não pode mais mudar de status.</p>
          )}
        </div>
      </section>

      <section className="card p-6" aria-labelledby="history-title">
        <h2 id="history-title" className="text-base font-semibold text-slate-900">
          Histórico de status
        </h2>
        <ol className="mt-4 space-y-4">
          {history.map((entry) => (
            <li key={entry.id} className="flex gap-3">
              <span className="mt-1.5 h-2.5 w-2.5 shrink-0 rounded-full bg-brand-600" />
              <div className="text-sm">
                <p className="text-slate-800">
                  {entry.fromStatus ? (
                    <>
                      <span className="font-medium">{STATUS_LABELS[entry.fromStatus]}</span> →{' '}
                      <span className="font-medium">{STATUS_LABELS[entry.toStatus]}</span>
                    </>
                  ) : (
                    <>
                      Criada como <span className="font-medium">{STATUS_LABELS[entry.toStatus]}</span>
                    </>
                  )}
                </p>
                <p className="text-xs text-slate-500">
                  por {entry.changedByName} em {formatDateTime(entry.changedAt)}
                </p>
              </div>
            </li>
          ))}
        </ol>
      </section>

      <ConfirmDialog
        open={confirmingDelete}
        title="Excluir solicitação"
        message="Esta ação não pode ser desfeita. A solicitação e seu histórico serão removidos."
        confirmLabel="Excluir"
        loading={deleting}
        onConfirm={() => void handleDelete()}
        onCancel={() => setConfirmingDelete(false)}
      />
    </div>
  );
}
