import { useState } from 'react';
import type { FormEvent } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { FormField } from '../components/ui/FormField';
import { Spinner } from '../components/ui/Spinner';
import { useAuth } from '../contexts/AuthContext';
import { useToast } from '../contexts/ToastContext';
import { getErrorMessage, getFieldErrors } from '../utils/erros';

export function LoginPage() {
  const { signIn } = useAuth();
  const toast = useToast();
  const navigate = useNavigate();
  const location = useLocation();
  const redirectTo = (location.state as { from?: string } | null)?.from ?? '/';

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [formError, setFormError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  const validate = () => {
    const next: Record<string, string> = {};
    if (!email.trim()) next.email = 'Informe seu e-mail.';
    if (!password) next.password = 'Informe sua senha.';
    return next;
  };

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    const clientErrors = validate();
    setErrors(clientErrors);
    setFormError(null);
    if (Object.keys(clientErrors).length > 0) return;

    setSubmitting(true);
    try {
      await signIn(email.trim(), password);
      toast.success('Login realizado com sucesso.');
      navigate(redirectTo, { replace: true });
    } catch (error) {
      setErrors(getFieldErrors(error));
      setFormError(getErrorMessage(error));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="flex min-h-screen items-center justify-center px-4 py-10">
      <div className="w-full max-w-md">
        <div className="mb-6 text-center">
          <span className="mx-auto flex h-12 w-12 items-center justify-center rounded-xl bg-brand-700 text-lg font-bold text-white">
            CS
          </span>
          <h1 className="mt-4 text-2xl font-semibold text-slate-900">Central de Solicitações</h1>
          <p className="mt-1 text-sm text-slate-500">Entre para registrar e acompanhar suas solicitações.</p>
        </div>

        <form onSubmit={handleSubmit} noValidate className="card space-y-4 p-6">
          {formError && (
            <div role="alert" className="rounded-lg border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-700">
              {formError}
            </div>
          )}

          <FormField id="email" label="E-mail" error={errors.email}>
            <input
              id="email"
              type="email"
              autoComplete="email"
              className={`input ${errors.email ? 'input-error' : ''}`}
              value={email}
              onChange={(event) => setEmail(event.target.value)}
              disabled={submitting}
              placeholder="voce@empresa.com"
            />
          </FormField>

          <FormField id="password" label="Senha" error={errors.password}>
            <input
              id="password"
              type="password"
              autoComplete="current-password"
              className={`input ${errors.password ? 'input-error' : ''}`}
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              disabled={submitting}
            />
          </FormField>

          <button type="submit" className="btn-primary w-full" disabled={submitting}>
            {submitting && <Spinner className="h-4 w-4" />}
            Entrar
          </button>
        </form>

        <p className="mt-4 text-center text-sm text-slate-600">
          Ainda não tem conta?{' '}
          <Link to="/register" className="font-medium text-brand-700 hover:underline">
            Cadastre-se
          </Link>
        </p>
      </div>
    </div>
  );
}
