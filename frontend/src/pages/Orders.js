import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import axios from 'axios';

function Orders() {
  const [orderPlaced, setOrderPlaced] = useState(false);

  const placeOrder = async () => {
    try {
      const response = await axios.post('/api/orders', {
        userId: 1,
        paymentMethod: 'CREDIT_CARD',
        deliveryAddress: '123 Main St'
      });
      if (response.data.success) {
        setOrderPlaced(true);
        alert('Order placed successfully!');
      }
    } catch (error) {
      alert('Error placing order');
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
      <h1>Orders</h1>
      {!orderPlaced ? (
        <div className="card">
          <h2>Place Your Order</h2>
          <button className="btn btn-primary" onClick={placeOrder}>
            Place Order
          </button>
        </div>
      ) : (
        <div className="card">
          <h2>Order Confirmed!</h2>
          <p>Your order has been placed successfully.</p>
        </div>
      )}
    </div>
    </>
  );
}

export default Orders;
