import { useState } from 'react';
import type { FormEvent } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { FormField } from '../components/ui/FormField';
import { Spinner } from '../components/ui/Spinner';
import { useAuth } from '../contexts/AuthContext';
import { useToast } from '../contexts/ToastContext';
import { getErrorMessage, getFieldErrors } from '../utils/erros';

const EMAIL_PATTERN = /^[^@\s]+@[^@\s]+\.[^@\s]+$/;

export function RegisterPage() {
  const { signUp } = useAuth();
  const toast = useToast();
  const navigate = useNavigate();

  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmation, setConfirmation] = useState('');
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [formError, setFormError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  const validate = () => {
    const next: Record<string, string> = {};
    if (!name.trim()) next.name = 'Informe seu nome.';
    if (!email.trim()) next.email = 'Informe seu e-mail.';
    else if (!EMAIL_PATTERN.test(email.trim())) next.email = 'Informe um e-mail válido.';
    if (password.length < 8 || password.length > 72) next.password = 'A senha deve ter entre 8 e 72 caracteres.';
    if (confirmation !== password) next.confirmation = 'As senhas não coincidem.';
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
      await signUp(name.trim(), email.trim(), password);
      toast.success('Conta criada com sucesso. Bem-vindo(a)!');
      navigate('/', { replace: true });
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
          <h1 className="text-2xl font-semibold text-slate-900">Criar conta</h1>
          <p className="mt-1 text-sm text-slate-500">Cadastre-se para abrir e acompanhar solicitações.</p>
        </div>

        <form onSubmit={handleSubmit} noValidate className="card space-y-4 p-6">
          {formError && (
            <div role="alert" className="rounded-lg border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-700">
              {formError}
            </div>
          )}

          <FormField id="name" label="Nome" error={errors.name}>
            <input
              id="name"
              type="text"
              autoComplete="name"
              maxLength={100}
              className={`input ${errors.name ? 'input-error' : ''}`}
              value={name}
              onChange={(event) => setName(event.target.value)}
              disabled={submitting}
            />
          </FormField>

          <FormField id="email" label="E-mail" error={errors.email}>
            <input
              id="email"
              type="email"
              autoComplete="email"
              className={`input ${errors.email ? 'input-error' : ''}`}
              value={email}
              onChange={(event) => setEmail(event.target.value)}
              disabled={submitting}
            />
          </FormField>

          <FormField id="password" label="Senha" error={errors.password} hint="Mínimo de 8 caracteres.">
            <input
              id="password"
              type="password"
              autoComplete="new-password"
              className={`input ${errors.password ? 'input-error' : ''}`}
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              disabled={submitting}
            />
          </FormField>

          <FormField id="confirmation" label="Confirmar senha" error={errors.confirmation}>
            <input
              id="confirmation"
              type="password"
              autoComplete="new-password"
              className={`input ${errors.confirmation ? 'input-error' : ''}`}
              value={confirmation}
              onChange={(event) => setConfirmation(event.target.value)}
              disabled={submitting}
            />
          </FormField>

          <button type="submit" className="btn-primary w-full" disabled={submitting}>
            {submitting && <Spinner className="h-4 w-4" />}
            Criar conta
          </button>
        </form>

        <p className="mt-4 text-center text-sm text-slate-600">
          Já tem conta?{' '}
          <Link to="/login" className="font-medium text-brand-700 hover:underline">
            Entrar
          </Link>
        </p>
      </div>
    </div>
  );
}
