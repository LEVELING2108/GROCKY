import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useCart } from '../context/CartContext';
import { useAuth } from '../context/AuthContext';
import { ShoppingBag, Trash2, Plus, Minus, ArrowRight, Package } from 'lucide-react';
import '../styles/Cart.css';

const Cart: React.FC = () => {
  const { items, totalItems, totalPrice, updateQuantity, removeFromCart, clearCart } = useCart();
  const { isAuthenticated } = useAuth();
  const navigate = useNavigate();

  const handleCheckout = () => {
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }
    navigate('/checkout');
  };

  if (items.length === 0) {
    return (
      <div className="cart-page">
        <div className="empty-cart container">
          <Package size={80} color="#2ECC71" />
          <h2>Your cart is empty</h2>
          <p>Add some fresh groceries to get started!</p>
          <Link to="/products" className="btn btn-primary btn-lg">
            Start Shopping <ArrowRight size={20} />
          </Link>
        </div>
      </div>
    );
  }

  return (
    <div className="cart-page">
      <div className="container">
        <h1 className="page-title">Shopping Cart</h1>

        <div className="cart-content">
          <div className="cart-items">
            {items.map((item) => (
              <div key={item.id} className="cart-item card">
                <div className="cart-item-image">
                  <img src={item.imageUrl || 'https://via.placeholder.com/100'} alt={item.name} />
                </div>

                <div className="cart-item-details">
                  <h3>{item.name}</h3>
                  <p className="item-price">${item.price.toFixed(2)}</p>
                </div>

                <div className="cart-item-quantity">
                  <button
                    onClick={() => updateQuantity(item.id, item.quantity - 1)}
                    className="qty-btn"
                    disabled={item.quantity <= 1}
                  >
                    <Minus size={16} />
                  </button>
                  <span className="qty-value">{item.quantity}</span>
                  <button
                    onClick={() => updateQuantity(item.id, item.quantity + 1)}
                    className="qty-btn"
                  >
                    <Plus size={16} />
                  </button>
                </div>

                <div className="cart-item-total">
                  <p>${(item.price * item.quantity).toFixed(2)}</p>
                </div>

                <button
                  onClick={() => removeFromCart(item.id)}
                  className="remove-btn"
                >
                  <Trash2 size={20} />
                </button>
              </div>
            ))}
          </div>

          <div className="cart-summary card">
            <h2>Order Summary</h2>

            <div className="summary-row">
              <span>Subtotal ({totalItems} items)</span>
              <span>${totalPrice.toFixed(2)}</span>
            </div>

            <div className="summary-row">
              <span>Delivery Fee</span>
              <span>{totalPrice >= 50 ? 'FREE' : '$5.99'}</span>
            </div>

            <div className="summary-row">
              <span>Tax (8%)</span>
              <span>${(totalPrice * 0.08).toFixed(2)}</span>
            </div>

            {totalPrice < 50 && (
              <div className="free-delivery-hint">
                Add ${(50 - totalPrice).toFixed(2)} more for FREE delivery!
              </div>
            )}

            <hr />

            <div className="summary-row total">
              <span>Total</span>
              <span>
                ${(
                  totalPrice +
                  (totalPrice >= 50 ? 0 : 5.99) +
                  totalPrice * 0.08
                ).toFixed(2)}
              </span>
            </div>

            <button onClick={handleCheckout} className="btn btn-primary btn-block btn-lg">
              Proceed to Checkout <ArrowRight size={20} />
            </button>

            <button onClick={clearCart} className="btn btn-outline btn-block">
              Clear Cart
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Cart;
