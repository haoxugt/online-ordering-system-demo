import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

function Cart() {
  const [cart, setCart] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    fetchCart();
  }, []);

  const fetchCart = async () => {
    try {
      const response = await axios.get('/api/cart/1');
      if (response.data.success) {
        setCart(response.data.data);
      }
    } catch (error) {
      console.error('Error fetching cart:', error);
    }
  };

  const removeItem = async (itemId) => {
    try {
      await axios.delete(`/api/cart/1/items/${itemId}`);
      fetchCart();
    } catch (error) {
      alert('Error removing item');
    }
  };

  const checkout = () => {
    navigate('/orders');
  };

  if (!cart) return <div className="container">Loading...</div>;

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
      <h1>Shopping Cart</h1>
      {cart.items.length === 0 ? (
        <p>Your cart is empty</p>
      ) : (
        <>
          {cart.items.map(item => (
            <div key={item.menuItemId} className="card">
              <h3>{item.name}</h3>
              <p>Price: ${item.price}</p>
              <p>Quantity: {item.quantity}</p>
              <p>Subtotal: ${item.subtotal}</p>
              <button className="btn btn-danger" onClick={() => removeItem(item.menuItemId)}>
                Remove
              </button>
            </div>
          ))}
          <div className="card">
            <h2>Total: ${cart.totalAmount}</h2>
            <button className="btn btn-primary" onClick={checkout}>
              Proceed to Checkout
            </button>
          </div>
        </>
      )}
    </div>
    </>

  );
}

export default Cart;
