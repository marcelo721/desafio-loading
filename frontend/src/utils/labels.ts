import type { AuditOperation, Priority, RequestStatus, SortDirection, SortField } from '../types';

export const STATUS_ORDER: RequestStatus[] = ['OPEN', 'IN_PROGRESS', 'COMPLETED'];

export const STATUS_LABELS: Record<RequestStatus, string> = {
  OPEN: 'Aberta',
  IN_PROGRESS: 'Em andamento',
  COMPLETED: 'Concluída',
};

export const PRIORITY_LABELS: Record<Priority, string> = {
  LOW: 'Baixa',
  MEDIUM: 'Média',
  HIGH: 'Alta',
};

export const STATUS_STYLES: Record<RequestStatus, string> = {
  OPEN: 'bg-sky-50 text-sky-700 ring-sky-200',
  IN_PROGRESS: 'bg-amber-50 text-amber-700 ring-amber-200',
  COMPLETED: 'bg-emerald-50 text-emerald-700 ring-emerald-200',
};

export const PRIORITY_STYLES: Record<Priority, string> = {
  LOW: 'bg-slate-100 text-slate-700 ring-slate-200',
  MEDIUM: 'bg-orange-50 text-orange-700 ring-orange-200',
  HIGH: 'bg-red-50 text-red-700 ring-red-200',
};

export const OPERATION_LABELS: Record<AuditOperation, string> = {
  CREATE: 'Criação',
  UPDATE: 'Edição',
  DELETE: 'Exclusão',
  STATUS_CHANGE: 'Mudança de status',
  REGISTER: 'Cadastro de usuário',
  LOGIN: 'Login',
};

export interface SortOption {
  value: string;
  label: string;
  sortBy: SortField;
  direction: SortDirection;
}

export const SORT_OPTIONS: SortOption[] = [
  { value: 'createdAt:DESC', label: 'Mais recentes', sortBy: 'createdAt', direction: 'DESC' },
  { value: 'createdAt:ASC', label: 'Mais antigas', sortBy: 'createdAt', direction: 'ASC' },
  { value: 'updatedAt:DESC', label: 'Atualizadas recentemente', sortBy: 'updatedAt', direction: 'DESC' },
  { value: 'priority:DESC', label: 'Prioridade (alta primeiro)', sortBy: 'priority', direction: 'DESC' },
  { value: 'priority:ASC', label: 'Prioridade (baixa primeiro)', sortBy: 'priority', direction: 'ASC' },
  { value: 'title:ASC', label: 'Título (A-Z)', sortBy: 'title', direction: 'ASC' },
  { value: 'title:DESC', label: 'Título (Z-A)', sortBy: 'title', direction: 'DESC' },
  { value: 'status:ASC', label: 'Status (aberta primeiro)', sortBy: 'status', direction: 'ASC' },
];

export function nextStatus(status: RequestStatus): RequestStatus | null {
  const index = STATUS_ORDER.indexOf(status);
  return index >= 0 && index < STATUS_ORDER.length - 1 ? STATUS_ORDER[index + 1] : null;
}
