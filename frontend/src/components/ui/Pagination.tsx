interface PaginationProps {
  page: number;
  totalPages: number;
  totalElements: number;
  size: number;
  disabled?: boolean;
  onChange: (page: number) => void;
}

export function Pagination({ page, totalPages, totalElements, size, disabled = false, onChange }: PaginationProps) {
  if (totalElements === 0) return null;

  const first = page * size + 1;
  const last = Math.min((page + 1) * size, totalElements);

  return (
    <nav className="flex flex-col items-center justify-between gap-3 sm:flex-row" aria-label="Paginação">
      <p className="text-sm text-slate-600">
        Exibindo {first}-{last} de {totalElements}
      </p>
      <div className="flex items-center gap-2">
        <button type="button" className="btn-secondary" disabled={disabled || page === 0} onClick={() => onChange(page - 1)}>
          Anterior
        </button>
        <span className="px-2 text-sm text-slate-600">
          Página {page + 1} de {Math.max(totalPages, 1)}
        </span>
        <button
          type="button"
          className="btn-secondary"
          disabled={disabled || page + 1 >= totalPages}
          onClick={() => onChange(page + 1)}
        >
          Próxima
        </button>
      </div>
    </nav>
  );
}
