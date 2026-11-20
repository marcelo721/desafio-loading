import axios from 'axios';
import type { ApiErrorBody } from '../types';

export function getApiError(error: unknown): ApiErrorBody | null {
  if (axios.isAxiosError<ApiErrorBody>(error) && error.response?.data && typeof error.response.data === 'object') {
    return error.response.data;
  }
  return null;
}

export function getErrorMessage(error: unknown): string {
  if (axios.isAxiosError(error) && !error.response) {
    return 'Não foi possível conectar ao servidor. Verifique sua conexão e tente novamente.';
  }
  const apiError = getApiError(error);
  if (apiError?.message) {
    return apiError.message;
  }
  return 'Ocorreu um erro inesperado. Tente novamente.';
}

export function getFieldErrors(error: unknown): Record<string, string> {
  const violations = getApiError(error)?.fieldErrors ?? [];
  return violations.reduce<Record<string, string>>((accumulator, violation) => {
    accumulator[violation.field] = violation.message;
    return accumulator;
  }, {});
}
