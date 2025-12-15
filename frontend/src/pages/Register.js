import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import axios from 'axios';

function Register() {
  const [user, setUser] = useState({ username: '', email: '', password: '', phone: '' });
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post('/api/users/register', user);
      if (response.data.success) {
        alert('Registration successful!');
        navigate('/login');
      }
    } catch (error) {
      alert('Registration failed: ' + (error.response?.data?.message || error.message));
    }
  };

  return (
    <div className="container" style={{ maxWidth: '400px', marginTop: '100px' }}>
      <div className="card">
        <h2>Register</h2>
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Username</label>
            <input type="text" value={user.username} onChange={(e) => setUser({ ...user, username: e.target.value })} required />
          </div>
          <div className="form-group">
            <label>Email</label>
            <input type="email" value={user.email} onChange={(e) => setUser({ ...user, email: e.target.value })} required />
          </div>
          <div className="form-group">
            <label>Phone</label>
            <input type="tel" value={user.phone} onChange={(e) => setUser({ ...user, phone: e.target.value })} />
          </div>
          <div className="form-group">
            <label>Password</label>
            <input type="password" value={user.password} onChange={(e) => setUser({ ...user, password: e.target.value })} required />
          </div>
          <button type="submit" className="btn btn-primary">Register</button>
        </form>
        <p style={{ marginTop: '10px' }}>
          Already have an account? <Link to="/login">Login</Link>
        </p>
      </div>
    </div>
  );
}

export default Register;
