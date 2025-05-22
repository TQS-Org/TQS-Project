import { Routes, Route } from 'react-router-dom';
import LoginPage from './LoginPage';
import DriverPage from './DriverPage';
import OperatorPage from './OperatorPage';
import AdminPage from './AdminPage';
import ProtectedRoute from './ProtectedRoute';

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<LoginPage />} />
      
      <Route
        path="/driver"
        element={
          <ProtectedRoute requiredRole="EV_DRIVER">
            <DriverPage />
          </ProtectedRoute>
        }
      />

      <Route
        path="/operator"
        element={
          <ProtectedRoute requiredRole="OPERATOR">
            <OperatorPage />
          </ProtectedRoute>
        }
      />

      <Route
        path="/admin"
        element={
          <ProtectedRoute requiredRole="ADMIN">
            <AdminPage />
          </ProtectedRoute>
        }
      />
    </Routes>
  );
}
