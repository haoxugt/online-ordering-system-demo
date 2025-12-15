import React from 'react';
import { Link } from 'react-router-dom';

function Home() {
  return (
    <div>
      <div className="navbar">
        <h1>Online Ordering System</h1>
        <nav>
          <Link to="/menu">Menu</Link>
          <Link to="/cart">Cart</Link>
          <Link to="/orders">Orders</Link>
          <Link to="/login">Login</Link>
          <Link to="/register">Register</Link>
        </nav>
      </div>
      <div className="container" style={{ textAlign: 'center', marginTop: '100px' }}>
        <h1>Welcome to Our Restaurant</h1>
        <p>Order your favorite food online!</p>
        <Link to="/menu">
          <button className="btn btn-primary" style={{ marginTop: '20px' }}>
            Browse Menu
          </button>
        </Link>
      </div>
    </div>
  );
}

export default Home;
