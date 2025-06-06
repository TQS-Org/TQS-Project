import { Routes, Route } from 'react-router-dom';
import LoginPage from './LoginPage';
import DriverPage from './DriverPage';
import OperatorPage from './OperatorPage';
import AdminPage from './AdminPage';
import ProtectedRoute from './ProtectedRoute';
import SignupPage from './SignUpPage';
import Layout from './Layout';
import StationDetailsPage from './StationDetailsPage';
import ChargerDetailsPage from './ChargerDetailsPage';
import ClientBookingsPage from './ClientBookingsPage';
import CancelPage from './components/CancelPage';
import ChargingSessionPage from './ChargingSessionPage';


export default function App() {
  return (
    <Routes>
      <Route path="/" element={<LoginPage />} />
      
      <Route
        path="/driver"
        element={
          <ProtectedRoute requiredRole="EV_DRIVER">
            <Layout>
              <DriverPage />
            </Layout>
          </ProtectedRoute>
        }
      />

      <Route 
        path="/cancel" 
        element={
          <ProtectedRoute requiredRole="EV_DRIVER">
            <Layout>
              <CancelPage />
            </Layout>
          </ProtectedRoute>
        }
      />

      <Route 
        path="/cancel" 
        element={
          <ProtectedRoute requiredRole="EV_DRIVER">
            <Layout>
              <CancelPage />
            </Layout>
          </ProtectedRoute>
        }
      />

      <Route
        path="/operator"
        element={
          <ProtectedRoute requiredRole="OPERATOR">
            <Layout>
              <OperatorPage />
            </Layout>
          </ProtectedRoute>
        }
      />

      <Route
        path="/admin"
        element={
          <ProtectedRoute requiredRole="ADMIN">
            <Layout>
              <AdminPage />
            </Layout>
          </ProtectedRoute>
        }
      />
      
      <Route path="/signup" element={<SignupPage />} />

      <Route
        path="/stations/:id"
        element={
          <ProtectedRoute requiredRole={["EV_DRIVER", "ADMIN", "OPERATOR"]}>
            <Layout>
              <StationDetailsPage />
            </Layout>
          </ProtectedRoute>
        }
      />

      <Route
        path="/chargers/:id"
        element={
          <ProtectedRoute requiredRole="EV_DRIVER">
            <Layout>
              <ChargerDetailsPage />
            </Layout>
          </ProtectedRoute>
        }
      />

      <Route
        path="/client/bookings"
        element={
          <ProtectedRoute requiredRole="EV_DRIVER">
            <Layout>
              <ClientBookingsPage />
            </Layout>
          </ProtectedRoute>
        }
      />
      <Route
        path="/booking/:id/status"
        element={
          <ProtectedRoute requiredRole="EV_DRIVER">
            <Layout>
              <ChargingSessionPage />
            </Layout>
          </ProtectedRoute>
        }
      />

    </Routes>
  );
}
