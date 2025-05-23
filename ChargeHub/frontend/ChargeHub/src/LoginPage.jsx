import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import { getUserRoleFromToken } from './auth'; // Adjust the import path as necessary
import './LoginPage.css'; // Adjust the import path as necessary

export default function LoginPage() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const navigate = useNavigate();

  const handleLogin = async () => {
    try {
      const res = await axios.post('http://localhost:8080/api/auth/login', { email, password });
      const { token } = res.data;
  
      localStorage.setItem('token', token);
  
      // Now verify the token and get the role
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
        <div className="login-box">
          <h2 className="login-title">ChargeHub</h2>
          <input
            className="login-input"
            placeholder="Email"
            onChange={e => setEmail(e.target.value)}
          />
          <input
            className="login-input"
            type="password"
            placeholder="Password"
            onChange={e => setPassword(e.target.value)}
          />
          <button className="login-button" onClick={handleLogin}>
            Login
          </button>
        </div>
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
