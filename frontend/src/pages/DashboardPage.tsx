import { useCallback, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { getDashboardSummary } from '../api/dashboard';
import { ErrorState } from '../components/ui/ErrorState';
import { PageLoader } from '../components/ui/Spinner';
import { useAuth } from '../contexts/AuthContext';
import type { DashboardSummary, RequestStatus } from '../types';
import { getErrorMessage } from '../utils/erros';

interface Indicator {
  key: keyof DashboardSummary;
  label: string;
  accent: string;
  status?: RequestStatus;
}

const INDICATORS: Indicator[] = [
  { key: 'total', label: 'Total de solicitações', accent: 'border-l-brand-600' },
  { key: 'open', label: 'Abertas', accent: 'border-l-sky-500', status: 'OPEN' },
  { key: 'inProgress', label: 'Em andamento', accent: 'border-l-amber-500', status: 'IN_PROGRESS' },
  { key: 'completed', label: 'Concluídas', accent: 'border-l-emerald-500', status: 'COMPLETED' },
];

export function DashboardPage() {
  const { user, isAdmin } = useAuth();
  const [summary, setSummary] = useState<DashboardSummary | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  const load = useCallback(async (signal?: AbortSignal) => {
    setLoading(true);
    setError(null);
    try {
      setSummary(await getDashboardSummary(signal));
    } catch (loadError) {
      if (signal?.aborted) return;
      setError(getErrorMessage(loadError));
    } finally {
      if (!signal?.aborted) setLoading(false);
    }
  }, []);

  useEffect(() => {
    const controller = new AbortController();
    void load(controller.signal);
    return () => controller.abort();
  }, [load]);

  return (
    <div className="space-y-6">
      <div className="flex flex-col justify-between gap-3 sm:flex-row sm:items-end">
        <div>
          <h1 className="text-2xl font-semibold text-slate-900">Olá, {user?.name.split(' ')[0]}</h1>
          <p className="text-sm text-slate-500">
            {isAdmin ? 'Visão geral de todas as solicitações.' : 'Visão geral das suas solicitações.'}
          </p>
        </div>
        <Link to="/requests/new" className="btn-primary">
          Nova solicitação
        </Link>
      </div>

      {loading && <PageLoader message="Carregando indicadores..." />}
      {!loading && error && <ErrorState message={error} onRetry={() => void load()} />}

      {!loading && !error && summary && (
        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4">
          {INDICATORS.map((indicator) => {
            const target = indicator.status ? `/requests?status=${indicator.status}` : '/requests';
            return (
              <Link
                key={indicator.key}
                to={target}
                className={`card border-l-4 ${indicator.accent} p-5 transition hover:shadow-md`}
              >
                <p className="text-sm font-medium text-slate-500">{indicator.label}</p>
                <p className="mt-2 text-3xl font-semibold text-slate-900">{summary[indicator.key]}</p>
              </Link>
            );
          })}
        </div>
      )}

      {!loading && !error && summary && summary.total > 0 && (
        <div className="card p-5">
          <h2 className="text-sm font-semibold text-slate-700">Distribuição por status</h2>
          <div className="mt-3 flex h-3 overflow-hidden rounded-full bg-slate-100" role="img" aria-label="Distribuição por status">
            <div className="bg-sky-500" style={{ width: `${(summary.open / summary.total) * 100}%` }} />
            <div className="bg-amber-500" style={{ width: `${(summary.inProgress / summary.total) * 100}%` }} />
            <div className="bg-emerald-500" style={{ width: `${(summary.completed / summary.total) * 100}%` }} />
          </div>
          <div className="mt-3 flex flex-wrap gap-4 text-xs text-slate-600">
            <span className="flex items-center gap-1.5">
              <span className="h-2.5 w-2.5 rounded-full bg-sky-500" /> Abertas
            </span>
            <span className="flex items-center gap-1.5">
              <span className="h-2.5 w-2.5 rounded-full bg-amber-500" /> Em andamento
            </span>
            <span className="flex items-center gap-1.5">
              <span className="h-2.5 w-2.5 rounded-full bg-emerald-500" /> Concluídas
            </span>
          </div>
        </div>
      )}

      {!loading && !error && summary && summary.total === 0 && (
        <div className="card px-6 py-10 text-center text-sm text-slate-500">
          Você ainda não tem solicitações. Crie a primeira para começar.
        </div>
      )}
    </div>
  );
}
