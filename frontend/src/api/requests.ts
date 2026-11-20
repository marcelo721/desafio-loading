import { http } from './http';
import type {
  Page,
  RequestFilters,
  RequestStatus,
  ServiceRequest,
  ServiceRequestPayload,
  StatusHistoryEntry,
} from '../types';

export async function listRequests(filters: RequestFilters, signal?: AbortSignal): Promise<Page<ServiceRequest>> {
  const params: Record<string, string | number> = {
    sortBy: filters.sortBy,
    direction: filters.direction,
    page: filters.page,
    size: filters.size,
  };
  if (filters.status) params.status = filters.status;
  if (filters.priority) params.priority = filters.priority;
  if (filters.search.trim()) params.search = filters.search.trim();

  const { data } = await http.get<Page<ServiceRequest>>('/api/requests', { params, signal });
  return data;
}

export async function getRequest(id: string, signal?: AbortSignal): Promise<ServiceRequest> {
  const { data } = await http.get<ServiceRequest>(`/api/requests/${id}`, { signal });
  return data;
}

export async function createRequest(payload: ServiceRequestPayload): Promise<ServiceRequest> {
  const { data } = await http.post<ServiceRequest>('/api/requests', payload);
  return data;
}

export async function updateRequest(id: string, payload: ServiceRequestPayload): Promise<ServiceRequest> {
  const { data } = await http.put<ServiceRequest>(`/api/requests/${id}`, payload);
  return data;
}

export async function deleteRequest(id: string): Promise<void> {
  await http.delete(`/api/requests/${id}`);
}

export async function changeRequestStatus(id: string, status: RequestStatus): Promise<ServiceRequest> {
  const { data } = await http.patch<ServiceRequest>(`/api/requests/${id}/status`, { status });
  return data;
}

export async function getRequestHistory(id: string, signal?: AbortSignal): Promise<StatusHistoryEntry[]> {
  const { data } = await http.get<StatusHistoryEntry[]>(`/api/requests/${id}/history`, { signal });
  return data;
}
