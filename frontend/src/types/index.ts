export type RequestStatus = 'OPEN' | 'IN_PROGRESS' | 'COMPLETED';
export type Priority = 'LOW' | 'MEDIUM' | 'HIGH';
export type Role = 'ADMIN' | 'USER';
export type SortDirection = 'ASC' | 'DESC';
export type SortField = 'createdAt' | 'updatedAt' | 'title' | 'priority' | 'status';
export type AuditOperation = 'CREATE' | 'UPDATE' | 'DELETE' | 'STATUS_CHANGE' | 'REGISTER' | 'LOGIN';

export interface User {
  id: string;
  name: string;
  email: string;
  role: Role;
}

export interface AuthResponse {
  token: string;
  tokenType: string;
  user: User;
}

export interface ServiceRequest {
  id: string;
  title: string;
  description: string;
  category: string;
  priority: Priority;
  status: RequestStatus;
  ownerId: string;
  ownerName: string;
  createdAt: string;
  updatedAt: string;
}

export interface ServiceRequestPayload {
  title: string;
  description: string;
  category: string;
  priority: Priority;
}

export interface StatusHistoryEntry {
  id: string;
  fromStatus: RequestStatus | null;
  toStatus: RequestStatus;
  changedById: string;
  changedByName: string;
  changedAt: string;
}

export interface DashboardSummary {
  total: number;
  open: number;
  inProgress: number;
  completed: number;
}

export interface AuditEntry {
  id: string;
  entityType: 'SERVICE_REQUEST' | 'USER';
  entityId: string;
  operation: AuditOperation;
  userId: string;
  userName: string;
  occurredAt: string;
  details: string | null;
}

export interface Page<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface FieldViolation {
  field: string;
  message: string;
}

export interface ApiErrorBody {
  status: number;
  error: string;
  message: string;
  path: string;
  fieldErrors?: FieldViolation[];
}

export interface RequestFilters {
  status: RequestStatus | '';
  priority: Priority | '';
  search: string;
  sortBy: SortField;
  direction: SortDirection;
  page: number;
  size: number;
}
