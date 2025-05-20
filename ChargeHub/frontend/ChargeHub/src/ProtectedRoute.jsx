// ProtectedRoute.jsx
import { useEffect, useState } from 'react';
import { Navigate } from 'react-router-dom';
import { getUserRoleFromToken } from './auth';

export default function ProtectedRoute({ children, requiredRole }) {
  const [authorized, setAuthorized] = useState(null); // null = loading, true/false = known

  useEffect(() => {
    const check = async () => {
      const role = await getUserRoleFromToken();
      if (!role) {
        setAuthorized(false);
      } else {
        setAuthorized(role === requiredRole);
      }
    };
    check();
  }, [requiredRole]);

  if (authorized === null) return <div>Loading...</div>;
  if (!authorized) return <Navigate to="/unauthorized" />;

  return children;
}
