import { useCallback, useEffect, useState } from 'react';
import { listAuditEntries } from '../api/audit';
import { EmptyState, ErrorState } from '../components/ui/ErrorState';
import { Pagination } from '../components/ui/Pagination';
import { PageLoader } from '../components/ui/Spinner';
import type { AuditEntry, Page } from '../types';
import { getErrorMessage } from '../utils/erros';
import { formatDateTime } from '../utils/format';
import { OPERATION_LABELS } from '../utils/labels';

const PAGE_SIZE = 20;

export function AuditPage() {
  const [page, setPage] = useState(0);
  const [result, setResult] = useState<Page<AuditEntry> | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const load = useCallback(async (pageNumber: number, signal?: AbortSignal) => {
    setLoading(true);
    setError(null);
    try {
      setResult(await listAuditEntries(pageNumber, PAGE_SIZE, signal));
    } catch (loadError) {
      if (signal?.aborted) return;
      setError(getErrorMessage(loadError));
    } finally {
      if (!signal?.aborted) setLoading(false);
    }
  }, []);

  useEffect(() => {
    const controller = new AbortController();
    void load(page, controller.signal);
    return () => controller.abort();
  }, [load, page]);

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-semibold text-slate-900">Auditoria</h1>
        <p className="text-sm text-slate-500">Registro de operações realizadas, do mais recente para o mais antigo.</p>
      </div>

      {loading && !result && <PageLoader message="Carregando registros..." />}
      {error && <ErrorState message={error} onRetry={() => void load(page)} />}

      {!error && result && result.totalElements === 0 && (
        <EmptyState title="Nenhum registro de auditoria" description="As operações realizadas aparecerão aqui." />
      )}

      {!error && result && result.totalElements > 0 && (
        <div className="space-y-4">
          <div className={`card overflow-x-auto transition-opacity ${loading ? 'opacity-60' : ''}`}>
            <table className="min-w-full divide-y divide-slate-200 text-sm">
              <thead className="bg-slate-50 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">
                <tr>
                  <th scope="col" className="px-4 py-3">
                    Data
                  </th>
                  <th scope="col" className="px-4 py-3">
                    Usuário
                  </th>
                  <th scope="col" className="px-4 py-3">
                    Operação
                  </th>
                  <th scope="col" className="px-4 py-3">
                    Detalhes
                  </th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {result.content.map((entry) => (
                  <tr key={entry.id}>
                    <td className="whitespace-nowrap px-4 py-3 text-slate-600">{formatDateTime(entry.occurredAt)}</td>
                    <td className="whitespace-nowrap px-4 py-3 font-medium text-slate-800">{entry.userName}</td>
                    <td className="whitespace-nowrap px-4 py-3">
                      <span className="rounded-full bg-brand-50 px-2.5 py-0.5 text-xs font-medium text-brand-800">
                        {OPERATION_LABELS[entry.operation]}
                      </span>
                    </td>
                    <td className="px-4 py-3 text-slate-600">{entry.details ?? '-'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          <Pagination
            page={result.page}
            totalPages={result.totalPages}
            totalElements={result.totalElements}
            size={result.size}
            disabled={loading}
            onChange={setPage}
          />
        </div>
      )}
    </div>
  );
}
