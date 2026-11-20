import axios from 'axios';

export const AUTH_STORAGE_KEY = 'loading.auth';
export const SESSION_EXPIRED_EVENT = 'loading:session-expired';

export const http = axios.create({
  baseURL: import.meta.env.VITE_API_URL ?? '',
  headers: { 'Content-Type': 'application/json' },
  timeout: 15000,
});

function readStoredToken(): string | null {
  try {
    const raw = localStorage.getItem(AUTH_STORAGE_KEY);
    return raw ? (JSON.parse(raw) as { token?: string }).token ?? null : null;
  } catch {
    return null;
  }
}

http.interceptors.request.use((config) => {
  const token = readStoredToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

http.interceptors.response.use(
  (response) => response,
  (error: unknown) => {
    if (axios.isAxiosError(error) && error.response?.status === 401) {
      const isAuthRequest = error.config?.url?.startsWith('/api/auth/') ?? false;
      if (!isAuthRequest && readStoredToken()) {
        window.dispatchEvent(new Event(SESSION_EXPIRED_EVENT));
      }
    }
    return Promise.reject(error);
  },
);
