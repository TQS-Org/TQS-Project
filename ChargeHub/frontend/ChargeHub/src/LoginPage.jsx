import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { getUserRoleFromToken } from './auth'; // Adjust the import path as necessary
import './LoginPage.css'; // Adjust the import path as necessary
import CONFIG from '../config';

export default function LoginPage() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault(); // Prevent form submission from reloading the page

    try {
      const res = await axios.post(`${CONFIG.API_URL}auth/login`, { email, password });
      const { token } = res.data;

      localStorage.setItem('token', token);
      localStorage.setItem('email', email);

      const role = await getUserRoleFromToken();
      console.log("Role from token:", role);

      switch (role) {
        case 'EV_DRIVER':
          navigate('/driver');
          break;
        case 'OPERATOR':
          navigate('/operator');
          break;
        case 'ADMIN':
          navigate('/admin');
          break;
        default:
          alert('Unknown role');
      }
    } catch (err) {
      console.error(err);
      alert('Login failed');
    }
  };

  return (
    <div className="login-wrapper">
      <div className="login-container">
        <form className="login-box" onSubmit={handleLogin}>
          <h2 className="login-title">ChargeHub</h2>
          <input
            className="login-input"
            placeholder="Email"
            value={email}
            onChange={e => setEmail(e.target.value)}
          />
          <input
            className="login-input"
            type="password"
            placeholder="Password"
            value={password}
            onChange={e => setPassword(e.target.value)}
          />
          <button type="submit" className="login-button">
            Login
          </button>
        </form>
        <p className="signup-text">
          Don’t have an account?{' '}
          <span id="CreateAccount" className="signup-link" onClick={() => navigate('/signup')}>
            Create Account
          </span>
        </p>
      </div>
    </div>
  );
}
