import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import axios from 'axios';

function Menu() {
  const [menuItems, setMenuItems] = useState([]);

  useEffect(() => {
    fetchMenuItems();
  }, []);

  const fetchMenuItems = async () => {
    try {
      const response = await axios.get('/api/menu/items');
      if (response.data.success) {
        setMenuItems(response.data.data);
      }
    } catch (error) {
      console.error('Error fetching menu:', error);
    }
  };

  const addToCart = async (itemId) => {
    const token = localStorage.getItem('token');
    if (!token) {
      alert('Please login first');
      return;
    }
    try {
      await axios.post('/api/cart/1/items', { menuItemId: itemId, quantity: 1 });
      alert('Added to cart!');
    } catch (error) {
      alert('Error adding to cart');
    }
  };

  return (
    <>
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
    <div className="container">
      <h1>Menu</h1>
      <div className="grid">
        {menuItems.map(item => (
          <div key={item.id} className="card">
            <h3>{item.name}</h3>
            <p>{item.description}</p>
            <p><strong>${item.price}</strong></p>
            <button className="btn btn-primary" onClick={() => addToCart(item.id)}>
              Add to Cart
            </button>
          </div>
        ))}
      </div>
    </div>
    </>
  );
}

export default Menu;
