import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { CartProvider } from './context/CartContext';
import Header from './components/Header';
import Home from './pages/Home';
import Products from './pages/Products';
import Dashboard from './pages/Dashboard';
import Checkout from './pages/Checkout';
import Login from './pages/Login';
import Cart from './pages/Cart';
import OrderTracking from './pages/OrderTracking';

const App: React.FC = () => {
  return (
    <AuthProvider>
      <CartProvider>
        <Router>
          <div className="app">
            <Header />
            <main className="main-content">
              <Routes>
                <Route path="/" element={<Home />} />
                <Route path="/products" element={<Products />} />
                <Route path="/dashboard" element={<Dashboard />} />
                <Route path="/checkout" element={<Checkout />} />
                <Route path="/cart" element={<Cart />} />
                <Route path="/login" element={<Login />} />
                <Route path="/orders/:orderId" element={<OrderTracking />} />
                <Route path="/order-success" element={
                  <div className="container" style={{ padding: '80px 20px', textAlign: 'center' }}>
                    <h1 className="section-title">Order Placed Successfully!</h1>
                    <p style={{marginBottom: '30px'}}>Thank you for your order. We'll deliver it soon!</p>
                    <Link to="/" className="btn btn-primary btn-lg">Continue Shopping</Link>
                  </div>
                } />
              </Routes>
            </main>
            <footer className="footer" style={{
              backgroundColor: '#2C3E50',
              color: 'white',
              padding: '60px 0',
              marginTop: '100px'
            }}>
              <div className="container" style={{
                display: 'grid',
                gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
                gap: '40px'
              }}>
                <div>
                  <h2 style={{ marginBottom: '20px' }}>GROCKY<span>.</span></h2>
                  <p style={{ opacity: 0.7 }}>Fresh groceries delivered to your doorstep. Quality guaranteed.</p>
                </div>
                <div>
                  <h4 style={{ marginBottom: '20px' }}>Quick Links</h4>
                  <ul style={{ opacity: 0.7, display: 'flex', flexDirection: 'column', gap: '10px' }}>
                    <li>About Us</li>
                    <li>Contact</li>
                    <li>FAQs</li>
                    <li>Privacy Policy</li>
                  </ul>
                </div>
                <div>
                  <h4 style={{ marginBottom: '20px' }}>Contact Info</h4>
                  <ul style={{ opacity: 0.7, display: 'flex', flexDirection: 'column', gap: '10px' }}>
                    <li>Email: hello@grocky.com</li>
                    <li>Phone: +1 (234) 567-890</li>
                    <li>Address: 123 Fresh St, Farm City</li>
                  </ul>
                </div>
              </div>
            </footer>
          </div>
        </Router>
      </CartProvider>
    </AuthProvider>
  );
};

export default App;
