import { Navigate, Route, Routes } from 'react-router-dom';
import { AppLayout } from '../components/layout/AppLayout';
import { AuditPage } from '../pages/AuditPage';
import { DashboardPage } from '../pages/DashboardPage';
import { LoginPage } from '../pages/LoginPage';
import { NotFoundPage } from '../pages/NotFoundPage';
import { RegisterPage } from '../pages/RegisterPage';
import { RequestDetailPage } from '../pages/RequestDetailPage';
import { RequestFormPage } from '../pages/RequestFormPage';
import { RequestsPage } from '../pages/RequestsPage';
import { AdminRoute, GuestRoute, ProtectedRoute } from './guards';

export function AppRoutes() {
  return (
    <Routes>
      <Route element={<GuestRoute />}>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
      </Route>

      <Route element={<ProtectedRoute />}>
        <Route element={<AppLayout />}>
          <Route index element={<DashboardPage />} />
          <Route path="/requests" element={<RequestsPage />} />
          <Route path="/requests/new" element={<RequestFormPage />} />
          <Route path="/requests/:id" element={<RequestDetailPage />} />
          <Route path="/requests/:id/edit" element={<RequestFormPage />} />
          <Route element={<AdminRoute />}>
            <Route path="/audit" element={<AuditPage />} />
          </Route>
          <Route path="/home" element={<Navigate to="/" replace />} />
          <Route path="*" element={<NotFoundPage />} />
        </Route>
      </Route>
    </Routes>
  );
}
