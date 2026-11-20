import { useCallback, useEffect, useMemo, useState } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { listRequests } from '../api/requests';
import { PriorityBadge, StatusBadge } from '../components/ui/Badges';
import { EmptyState, ErrorState } from '../components/ui/ErrorState';
import { Pagination } from '../components/ui/Pagination';
import { PageLoader, Spinner } from '../components/ui/Spinner';
import { useAuth } from '../contexts/AuthContext';
import { useDebounce } from '../hooks/useDebounce';
import type { Page, Priority, RequestFilters, RequestStatus, ServiceRequest } from '../types';
import { getErrorMessage } from '../utils/erros';
import { formatDateTime } from '../utils/format';
import { PRIORITY_LABELS, SORT_OPTIONS, STATUS_LABELS, STATUS_ORDER } from '../utils/labels';

const PAGE_SIZE = 10;
const PRIORITIES: Priority[] = ['LOW', 'MEDIUM', 'HIGH'];
const DEFAULT_SORT = SORT_OPTIONS[0];

function readFilters(params: URLSearchParams): RequestFilters {
  const status = params.get('status');
  const priority = params.get('priority');
  const sortValue = `${params.get('sortBy') ?? ''}:${params.get('direction') ?? ''}`;
  const sort = SORT_OPTIONS.find((option) => option.value === sortValue) ?? DEFAULT_SORT;
  const page = Number.parseInt(params.get('page') ?? '0', 10);

  return {
    status: STATUS_ORDER.includes(status as RequestStatus) ? (status as RequestStatus) : '',
    priority: PRIORITIES.includes(priority as Priority) ? (priority as Priority) : '',
    search: params.get('search') ?? '',
    sortBy: sort.sortBy,
    direction: sort.direction,
    page: Number.isNaN(page) || page < 0 ? 0 : page,
    size: PAGE_SIZE,
  };
}

export function RequestsPage() {
  const { isAdmin } = useAuth();
  const [params, setParams] = useSearchParams();
  const filters = useMemo(() => readFilters(params), [params]);

  const [searchInput, setSearchInput] = useState(filters.search);
  const debouncedSearch = useDebounce(searchInput);

  const [result, setResult] = useState<Page<ServiceRequest> | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const updateParams = useCallback(
    (changes: Partial<Record<'status' | 'priority' | 'search' | 'sort' | 'page', string>>) => {
      setParams(
        (current) => {
          const next = new URLSearchParams(current);
          Object.entries(changes).forEach(([key, value]) => {
            if (key === 'sort') {
              const [sortBy, direction] = (value ?? '').split(':');
              if (value && value !== DEFAULT_SORT.value) {
                next.set('sortBy', sortBy);
                next.set('direction', direction);
              } else {
                next.delete('sortBy');
                next.delete('direction');
              }
            } else if (value) {
              next.set(key, value);
            } else {
              next.delete(key);
            }
          });
          if (!('page' in changes)) next.delete('page');
          return next;
        },
        { replace: true },
      );
    },
    [setParams],
  );

  useEffect(() => {
    if (debouncedSearch === searchInput && debouncedSearch.trim() !== filters.search.trim()) {
      updateParams({ search: debouncedSearch.trim() });
    }
  }, [debouncedSearch, searchInput, filters.search, updateParams]);

  useEffect(() => {
    const controller = new AbortController();
    setLoading(true);
    setError(null);
    listRequests(filters, controller.signal)
      .then((page) => {
        setResult(page);
        setLoading(false);
      })
      .catch((loadError: unknown) => {
        if (controller.signal.aborted) return;
        setError(getErrorMessage(loadError));
        setLoading(false);
      });
    return () => controller.abort();
  }, [filters]);

  const retry = () => setParams((current) => new URLSearchParams(current), { replace: true });

  const hasActiveFilters = Boolean(filters.status || filters.priority || filters.search);
  const sortValue = `${filters.sortBy}:${filters.direction}`;

  const clearFilters = () => {
    setSearchInput('');
    setParams(new URLSearchParams(), { replace: true });
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col justify-between gap-3 sm:flex-row sm:items-center">
        <div>
          <h1 className="text-2xl font-semibold text-slate-900">Solicitações</h1>
          <p className="text-sm text-slate-500">
            {isAdmin ? 'Todas as solicitações cadastradas.' : 'Solicitações cadastradas por você.'}
          </p>
        </div>
        <Link to="/requests/new" className="btn-primary">
          Nova solicitação
        </Link>
      </div>

      <section className="card grid grid-cols-1 gap-4 p-4 sm:grid-cols-2 lg:grid-cols-4" aria-label="Filtros">
        <div className="sm:col-span-2 lg:col-span-4">
          <label htmlFor="search" className="field-label">
            Buscar
          </label>
          <input
            id="search"
            type="search"
            className="input"
            placeholder="Título, descrição ou categoria"
            value={searchInput}
            onChange={(event) => setSearchInput(event.target.value)}
          />
        </div>

        <div>
          <label htmlFor="status" className="field-label">
            Status
          </label>
          <select
            id="status"
            className="input"
            value={filters.status}
            onChange={(event) => updateParams({ status: event.target.value })}
          >
            <option value="">Todos</option>
            {STATUS_ORDER.map((status) => (
              <option key={status} value={status}>
                {STATUS_LABELS[status]}
              </option>
            ))}
          </select>
        </div>

        <div>
          <label htmlFor="priority" className="field-label">
            Prioridade
          </label>
          <select
            id="priority"
            className="input"
            value={filters.priority}
            onChange={(event) => updateParams({ priority: event.target.value })}
          >
            <option value="">Todas</option>
            {PRIORITIES.map((priority) => (
              <option key={priority} value={priority}>
                {PRIORITY_LABELS[priority]}
              </option>
            ))}
          </select>
        </div>

        <div className="sm:col-span-2">
          <label htmlFor="sort" className="field-label">
            Ordenar por
          </label>
          <select
            id="sort"
            className="input"
            value={sortValue}
            onChange={(event) => updateParams({ sort: event.target.value })}
          >
            {SORT_OPTIONS.map((option) => (
              <option key={option.value} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        </div>

        {hasActiveFilters && (
          <div className="sm:col-span-2 lg:col-span-4">
            <button type="button" className="text-sm font-medium text-brand-700 hover:underline" onClick={clearFilters}>
              Limpar filtros
            </button>
          </div>
        )}
      </section>

      {loading && !result && <PageLoader message="Carregando solicitações..." />}
      {error && <ErrorState message={error} onRetry={retry} />}

      {!error && result && result.totalElements === 0 && !loading && (
        <EmptyState
          title={hasActiveFilters ? 'Nenhuma solicitação encontrada' : 'Nenhuma solicitação cadastrada'}
          description={
            hasActiveFilters
              ? 'Ajuste ou limpe os filtros para ver outros resultados.'
              : 'Crie sua primeira solicitação para começar o acompanhamento.'
          }
          action={
            hasActiveFilters ? (
              <button type="button" className="btn-secondary" onClick={clearFilters}>
                Limpar filtros
              </button>
            ) : (
              <Link to="/requests/new" className="btn-primary">
                Nova solicitação
              </Link>
            )
          }
        />
      )}

      {!error && result && result.totalElements > 0 && (
        <div className="space-y-4">
          <div className="relative">
            {loading && (
              <div className="absolute right-3 top-3 z-10 text-brand-600">
                <Spinner className="h-5 w-5" />
              </div>
            )}

            <ul className={`space-y-3 transition-opacity ${loading ? 'opacity-60' : ''}`}>
              {result.content.map((request) => (
                <li key={request.id}>
                  <Link
                    to={`/requests/${request.id}`}
                    className="card block p-4 transition hover:border-brand-300 hover:shadow-md"
                  >
                    <div className="flex flex-col gap-2 sm:flex-row sm:items-start sm:justify-between">
                      <div className="min-w-0">
                        <h2 className="truncate text-base font-semibold text-slate-900">{request.title}</h2>
                        <p className="mt-1 line-clamp-2 text-sm text-slate-600">{request.description}</p>
                      </div>
                      <div className="flex shrink-0 flex-wrap gap-2">
                        <StatusBadge status={request.status} />
                        <PriorityBadge priority={request.priority} />
                      </div>
                    </div>
                    <div className="mt-3 flex flex-wrap gap-x-4 gap-y-1 text-xs text-slate-500">
                      <span>Categoria: {request.category}</span>
                      {isAdmin && <span>Solicitante: {request.ownerName}</span>}
                      <span>Criada em {formatDateTime(request.createdAt)}</span>
                    </div>
                  </Link>
                </li>
              ))}
            </ul>
          </div>

          <Pagination
            page={result.page}
            totalPages={result.totalPages}
            totalElements={result.totalElements}
            size={result.size}
            disabled={loading}
            onChange={(page) => updateParams({ page: String(page) })}
          />
        </div>
      )}
    </div>
  );
}
