import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { ShoppingCart, User, Search, LogOut, Menu } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { useCart } from '../context/CartContext';
import '../styles/Header.css';

const Header: React.FC = () => {
  const { user, logout, isAuthenticated } = useAuth();
  const { totalItems } = useCart();
  const [searchQuery, setSearchQuery] = useState('');
  const navigate = useNavigate();

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    if (searchQuery.trim()) {
      navigate(`/products?search=${searchQuery}`);
    }
  };

  return (
    <header className="header">
      <div className="container header-container">
        <Link to="/" className="logo">
          GROCKY<span>.</span>
        </Link>

        <form className="search-bar" onSubmit={handleSearch}>
          <input 
            type="text" 
            placeholder="Search fresh groceries..." 
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
          <button type="submit"><Search size={20} /></button>
        </form>

        <nav className="nav-links">
          <Link to="/products" className="nav-link">Shop</Link>
          {user?.role === 'ADMIN' && <Link to="/dashboard" className="nav-link">Admin</Link>}
        </nav>

        <div className="header-actions">
          <Link to="/cart" className="cart-icon">
            <ShoppingCart size={24} />
            {totalItems > 0 && <span className="cart-badge">{totalItems}</span>}
          </Link>

          {isAuthenticated ? (
            <div className="user-menu">
              <span className="user-name">Hi, {user?.name ? user.name.split(' ')[0] : 'User'}</span>
              <button onClick={logout} className="logout-btn"><LogOut size={20} /></button>
            </div>
          ) : (
            <Link to="/login" className="btn btn-primary login-btn">
              <User size={20} /> Login
            </Link>
          )}
          
          <button className="mobile-menu-btn"><Menu size={24} /></button>
        </div>
      </div>
    </header>
  );
};

export default Header;
