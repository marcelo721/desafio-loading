import { useState } from 'react';
import { Link, NavLink, Outlet, useNavigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';

const LINK_BASE = 'rounded-lg px-3 py-2 text-sm font-medium transition';

function navClass({ isActive }: { isActive: boolean }) {
  return `${LINK_BASE} ${isActive ? 'bg-brand-50 text-brand-800' : 'text-slate-600 hover:bg-slate-100'}`;
}

export function AppLayout() {
  const { user, isAdmin, signOut } = useAuth();
  const navigate = useNavigate();
  const [menuOpen, setMenuOpen] = useState(false);

  const handleSignOut = () => {
    signOut();
    navigate('/login', { replace: true });
  };

  const closeMenu = () => setMenuOpen(false);

  return (
    <div className="min-h-screen">
      <header className="sticky top-0 z-30 border-b border-slate-200 bg-white/90 backdrop-blur">
        <div className="mx-auto flex max-w-6xl items-center justify-between px-4 py-3 sm:px-6">
          <Link to="/" className="flex items-center gap-2 text-base font-semibold text-slate-900" onClick={closeMenu}>
            <span className="flex h-8 w-8 items-center justify-center rounded-lg bg-brand-700 text-sm font-bold text-white">
              CS
            </span>
            Central de Solicitações
          </Link>

          <button
            type="button"
            className="btn-secondary px-3 py-1.5 md:hidden"
            aria-expanded={menuOpen}
            aria-controls="main-menu"
            onClick={() => setMenuOpen((open) => !open)}
          >
            Menu
          </button>

          <nav id="main-menu" className="hidden items-center gap-1 md:flex" aria-label="Principal">
            <NavLink to="/" end className={navClass}>
              Dashboard
            </NavLink>
            <NavLink to="/requests" className={navClass}>
              Solicitações
            </NavLink>
            {isAdmin && (
              <NavLink to="/audit" className={navClass}>
                Auditoria
              </NavLink>
            )}
            <div className="mx-3 h-6 w-px bg-slate-200" />
            <div className="text-right leading-tight">
              <p className="text-sm font-medium text-slate-800">{user?.name}</p>
              <p className="text-xs text-slate-500">{isAdmin ? 'Administrador' : 'Usuário'}</p>
            </div>
            <button type="button" className="btn-secondary ml-2" onClick={handleSignOut}>
              Sair
            </button>
          </nav>
        </div>

        {menuOpen && (
          <nav className="flex flex-col gap-1 border-t border-slate-200 px-4 py-3 md:hidden" aria-label="Principal">
            <NavLink to="/" end className={navClass} onClick={closeMenu}>
              Dashboard
            </NavLink>
            <NavLink to="/requests" className={navClass} onClick={closeMenu}>
              Solicitações
            </NavLink>
            {isAdmin && (
              <NavLink to="/audit" className={navClass} onClick={closeMenu}>
                Auditoria
              </NavLink>
            )}
            <div className="mt-2 flex items-center justify-between border-t border-slate-100 pt-3">
              <div className="leading-tight">
                <p className="text-sm font-medium text-slate-800">{user?.name}</p>
                <p className="text-xs text-slate-500">{isAdmin ? 'Administrador' : 'Usuário'}</p>
              </div>
              <button type="button" className="btn-secondary" onClick={handleSignOut}>
                Sair
              </button>
            </div>
          </nav>
        )}
      </header>

      <main className="mx-auto max-w-6xl px-4 py-6 sm:px-6 sm:py-8">
        <Outlet />
      </main>
    </div>
  );
}
