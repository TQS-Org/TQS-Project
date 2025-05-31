// auth.js
import axios from 'axios';
import CONFIG from '../config'; 

export async function getUserRoleFromToken() {
  const token = localStorage.getItem('token');
  if (!token) return null;

  try {
    const res = await axios.get(`${CONFIG.API_URL}auth/validate`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    console.log("Role response:", res.data);
    return res.data.role;
  } catch (err) {
    console.error(err);
    return null; // Token invalid or expired
  }
}
