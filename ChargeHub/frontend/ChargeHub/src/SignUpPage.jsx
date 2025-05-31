import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import './SignupPage.css'; // Still uses a separate stylesheet
import CONFIG from '../config'; 

export default function SignupPage() {
  const [name, setName] = useState('');
  const [age, setAge] = useState('');
  const [number, setNumber] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const [errors, setErrors] = useState({});
  const navigate = useNavigate();

  const validate = () => {
    const newErrors = {};

    if (!name.trim()) newErrors.name = "Name is required";
    if (!age || isNaN(age) || age < 18 || age > 120) newErrors.age = "Enter a valid age (18–120)";
    if (!/^9\d{8}$/.test(number)) newErrors.number = "Phone number must be exactly 9 digits and start with 9";
    if (!/^\S+@\S+\.\S+$/.test(email)) newErrors.email = "Enter a valid email";
    if (!/^(?=.*[A-Za-z])(?=.*\d).{8,}$/.test(password)) newErrors.password = "Password must be at least 8 characters long and include both letters and numbers";

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSignup = async () => {
    if (!validate()) return;

    try {
      const res = await axios.post(`${CONFIG.API_URL}auth/register`, {
        name,
        age,
        number,
        mail: email,
        password,
      });

      const { token } = res.data;
      localStorage.setItem('token', token);
      navigate('/driver');
    } catch (err) {
      console.error(err);
      setErrors({ api: 'Signup failed. Please try again.' });
    }
  };

  return (
    <div className="signup-wrapper">
      <div className="signup-container">
        <div className="signup-box">
          <h2 className="signup-title">Create Account</h2>

          <input
            className={`signup-input ${errors.name ? 'error' : ''}`}
            placeholder="Name"
            onChange={e => setName(e.target.value)}
          />
          {errors.name && <p className="error-text">{errors.name}</p>}

          <input
            className={`signup-input ${errors.age ? 'error' : ''}`}
            placeholder="Age"
            type="number"
            onChange={e => setAge(e.target.value)}
          />
          {errors.age && <p className="error-text">{errors.age}</p>}

          <input
            className={`signup-input ${errors.number ? 'error' : ''}`}
            placeholder="Phone Number"
            onChange={e => setNumber(e.target.value)}
          />
          {errors.number && <p className="error-text">{errors.number}</p>}

          <input
            className={`signup-input ${errors.email ? 'error' : ''}`}
            placeholder="Email"
            onChange={e => setEmail(e.target.value)}
          />
          {errors.email && <p className="error-text">{errors.email}</p>}

          <input
            className={`signup-input ${errors.password ? 'error' : ''}`}
            type="password"
            placeholder="Password"
            onChange={e => setPassword(e.target.value)}
          />
          {errors.password && <p className="error-text">{errors.password}</p>}

          {errors.api && <p className="error-text">{errors.api}</p>}

          <button className="signup-button" onClick={handleSignup}>
            Sign Up
          </button>
        </div>
        <p className="login-text">
          Already have an account?{' '}
          <span className="login-link" onClick={() => navigate('/')}>
            Login
          </span>
        </p>
      </div>
    </div>
  );
}
