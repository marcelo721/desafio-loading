import { http } from './http';
import type { AuditEntry, Page } from '../types';

export async function listAuditEntries(page: number, size: number, signal?: AbortSignal): Promise<Page<AuditEntry>> {
  const { data } = await http.get<Page<AuditEntry>>('/api/audit', { params: { page, size }, signal });
  return data;
}
