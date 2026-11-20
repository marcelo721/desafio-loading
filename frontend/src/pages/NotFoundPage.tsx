import { Link } from 'react-router-dom';
import { EmptyState } from '../components/ui/ErrorState';

export function NotFoundPage() {
  return (
    <EmptyState
      title="Página não encontrada"
      description="O endereço acessado não existe ou foi movido."
      action={
        <Link to="/" className="btn-primary">
          Voltar ao início
        </Link>
      }
    />
  );
}
