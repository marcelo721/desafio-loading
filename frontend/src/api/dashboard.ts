import { http } from './http';
import type { DashboardSummary } from '../types';

export async function getDashboardSummary(signal?: AbortSignal): Promise<DashboardSummary> {
  const { data } = await http.get<DashboardSummary>('/api/dashboard/summary', { signal });
  return data;
}
