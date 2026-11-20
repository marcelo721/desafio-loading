import { useEffect, useState } from 'react';
import type { FormEvent } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { createRequest, getRequest, updateRequest } from '../api/requests';
import { ErrorState } from '../components/ui/ErrorState';
import { FormField } from '../components/ui/FormField';
import { PageLoader, Spinner } from '../components/ui/Spinner';
import { useToast } from '../contexts/ToastContext';
import type { Priority, ServiceRequestPayload } from '../types';
import { getErrorMessage, getFieldErrors } from '../utils/erros';
import { PRIORITY_LABELS } from '../utils/labels';

const TITLE_MAX = 120;
const DESCRIPTION_MAX = 2000;
const CATEGORY_MAX = 60;
const PRIORITIES: Priority[] = ['LOW', 'MEDIUM', 'HIGH'];

const EMPTY_FORM: ServiceRequestPayload = { title: '', description: '', category: '', priority: 'MEDIUM' };

function validate(form: ServiceRequestPayload): Record<string, string> {
  const errors: Record<string, string> = {};
  if (!form.title.trim()) errors.title = 'O título é obrigatório.';
  else if (form.title.trim().length > TITLE_MAX) errors.title = `O título deve ter no máximo ${TITLE_MAX} caracteres.`;
  if (!form.description.trim()) errors.description = 'A descrição é obrigatória.';
  else if (form.description.trim().length > DESCRIPTION_MAX)
    errors.description = `A descrição deve ter no máximo ${DESCRIPTION_MAX} caracteres.`;
  if (!form.category.trim()) errors.category = 'A categoria é obrigatória.';
  else if (form.category.trim().length > CATEGORY_MAX)
    errors.category = `A categoria deve ter no máximo ${CATEGORY_MAX} caracteres.`;
  return errors;
}

export function RequestFormPage() {
  const { id } = useParams<{ id: string }>();
  const isEditing = Boolean(id);
  const navigate = useNavigate();
  const toast = useToast();

  const [form, setForm] = useState<ServiceRequestPayload>(EMPTY_FORM);
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [formError, setFormError] = useState<string | null>(null);
  const [loading, setLoading] = useState(isEditing);
  const [loadError, setLoadError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (!id) return undefined;
    const controller = new AbortController();
    setLoading(true);
    setLoadError(null);
    getRequest(id, controller.signal)
      .then((request) => {
        setForm({
          title: request.title,
          description: request.description,
          category: request.category,
          priority: request.priority,
        });
        setLoading(false);
      })
      .catch((error: unknown) => {
        if (controller.signal.aborted) return;
        setLoadError(getErrorMessage(error));
        setLoading(false);
      });
    return () => controller.abort();
  }, [id]);

  const setField = <K extends keyof ServiceRequestPayload>(field: K, value: ServiceRequestPayload[K]) => {
    setForm((current) => ({ ...current, [field]: value }));
    setErrors((current) => {
      if (!current[field]) return current;
      const { [field]: _removed, ...rest } = current;
      return rest;
    });
  };

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    const clientErrors = validate(form);
    setErrors(clientErrors);
    setFormError(null);
    if (Object.keys(clientErrors).length > 0) return;

    const payload: ServiceRequestPayload = {
      title: form.title.trim(),
      description: form.description.trim(),
      category: form.category.trim(),
      priority: form.priority,
    };

    setSubmitting(true);
    try {
      const saved = id ? await updateRequest(id, payload) : await createRequest(payload);
      toast.success(id ? 'Solicitação atualizada com sucesso.' : 'Solicitação criada com sucesso.');
      navigate(`/requests/${saved.id}`, { replace: true });
    } catch (error) {
      setErrors(getFieldErrors(error));
      setFormError(getErrorMessage(error));
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) return <PageLoader message="Carregando solicitação..." />;
  if (loadError) return <ErrorState message={loadError} />;

  const cancelTarget = id ? `/requests/${id}` : '/requests';

  return (
    <div className="mx-auto max-w-2xl space-y-6">
      <div>
        <Link to={cancelTarget} className="text-sm text-slate-500 hover:text-slate-700">
          ← Voltar
        </Link>
        <h1 className="mt-2 text-2xl font-semibold text-slate-900">
          {isEditing ? 'Editar solicitação' : 'Nova solicitação'}
        </h1>
        {!isEditing && <p className="text-sm text-slate-500">Toda nova solicitação começa com o status “Aberta”.</p>}
      </div>

      <form onSubmit={handleSubmit} noValidate className="card space-y-5 p-6">
        {formError && (
          <div role="alert" className="rounded-lg border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-700">
            {formError}
          </div>
        )}

        <FormField id="title" label="Título" error={errors.title}>
          <input
            id="title"
            type="text"
            maxLength={TITLE_MAX + 20}
            className={`input ${errors.title ? 'input-error' : ''}`}
            value={form.title}
            onChange={(event) => setField('title', event.target.value)}
            disabled={submitting}
            placeholder="Resuma a solicitação em uma frase"
          />
        </FormField>

        <FormField id="description" label="Descrição" error={errors.description}>
          <textarea
            id="description"
            rows={5}
            className={`input ${errors.description ? 'input-error' : ''}`}
            value={form.description}
            onChange={(event) => setField('description', event.target.value)}
            disabled={submitting}
            placeholder="Descreva o que precisa ser feito"
          />
          <p className="mt-1 text-right text-xs text-slate-400">
            {form.description.length}/{DESCRIPTION_MAX}
          </p>
        </FormField>

        <div className="grid grid-cols-1 gap-5 sm:grid-cols-2">
          <FormField id="category" label="Categoria" error={errors.category}>
            <input
              id="category"
              type="text"
              className={`input ${errors.category ? 'input-error' : ''}`}
              value={form.category}
              onChange={(event) => setField('category', event.target.value)}
              disabled={submitting}
              placeholder="Ex.: Infraestrutura, Financeiro"
            />
          </FormField>

          <FormField id="priority" label="Prioridade" error={errors.priority}>
            <select
              id="priority"
              className={`input ${errors.priority ? 'input-error' : ''}`}
              value={form.priority}
              onChange={(event) => setField('priority', event.target.value as Priority)}
              disabled={submitting}
            >
              {PRIORITIES.map((priority) => (
                <option key={priority} value={priority}>
                  {PRIORITY_LABELS[priority]}
                </option>
              ))}
            </select>
          </FormField>
        </div>

        <div className="flex justify-end gap-3 border-t border-slate-100 pt-5">
          <Link to={cancelTarget} className="btn-secondary">
            Cancelar
          </Link>
          <button type="submit" className="btn-primary" disabled={submitting}>
            {submitting && <Spinner className="h-4 w-4" />}
            {isEditing ? 'Salvar alterações' : 'Criar solicitação'}
          </button>
        </div>
      </form>
    </div>
  );
}
