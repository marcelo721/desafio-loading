import { http } from './http';
import type { AuthResponse, User } from '../types';

export async function login(email: string, password: string): Promise<AuthResponse> {
  const { data } = await http.post<AuthResponse>('/api/auth/login', { email, password });
  return data;
}

export async function register(name: string, email: string, password: string): Promise<User> {
  const { data } = await http.post<User>('/api/auth/register', { name, email, password });
  return data;
}
