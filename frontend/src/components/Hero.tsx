import React from 'react';
import { ShoppingBag, ArrowRight } from 'lucide-react';
import { Link } from 'react-router-dom';
import '../styles/Hero.css';

const Hero: React.FC = () => {
  return (
    <section className="hero">
      <div className="container hero-container">
        <div className="hero-content fade-in">
          <span className="hero-badge">100% Organic & Fresh</span>
          <h1>Freshness Delivered to Your <span>Doorstep</span></h1>
          <p>Get the best quality groceries, fruits, and vegetables from local farms at the best prices. Faster delivery, better quality.</p>
          <div className="hero-btns">
            <Link to="/products" className="btn btn-primary btn-lg">
              Shop Now <ShoppingBag size={20} />
            </Link>
            <Link to="/about" className="btn btn-outline btn-lg">
              Learn More <ArrowRight size={20} />
            </Link>
          </div>
          <div className="hero-stats">
            <div className="stat-item">
              <strong>15k+</strong>
              <span>Happy Customers</span>
            </div>
            <div className="stat-item">
              <strong>500+</strong>
              <span>Products</span>
            </div>
            <div className="stat-item">
              <strong>30min</strong>
              <span>Fast Delivery</span>
            </div>
          </div>
        </div>
        <div className="hero-image fade-in">
          <div className="image-wrapper">
             <img src="https://images.unsplash.com/photo-1542838132-92c53300491e?auto=format&fit=crop&q=80&w=1000" alt="Fresh Groceries" />
             <div className="floating-card c1">
                <div className="icon">🍎</div>
                <div className="info">
                  <strong>Fresh Fruits</strong>
                  <span>Direct from farm</span>
                </div>
             </div>
             <div className="floating-card c2">
                <div className="icon">🥬</div>
                <div className="info">
                  <strong>Leafy Greens</strong>
                  <span>Always organic</span>
                </div>
             </div>
          </div>
        </div>
      </div>
    </section>
  );
};

export default Hero;
