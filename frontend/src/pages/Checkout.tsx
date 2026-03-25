import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useCart } from '../context/CartContext';
import { useAuth } from '../context/AuthContext';
import { apiService } from '../services/apiService';
import { CreditCard, MapPin, Truck, DollarSign, CheckCircle, Loader2 } from 'lucide-react';
import '../styles/Checkout.css';

const Checkout: React.FC = () => {
  const { items, totalPrice, clearCart } = useCart();
  const { user } = useAuth();
  const navigate = useNavigate();

  const [loading, setLoading] = useState(false);
  const [step, setStep] = useState(1);
  const [formData, setFormData] = useState({
    address: user?.address || '',
    city: user?.city || '',
    state: user?.state || '',
    zipCode: user?.zipCode || '',
    instructions: '',
    scheduledDate: '',
  });

  const deliveryFee = totalPrice >= 50 ? 0 : 5.99;
  const tax = totalPrice * 0.08;
  const total = totalPrice + deliveryFee + tax;

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handlePlaceOrder = async () => {
    if (!user?.userId) {
      alert('Please login to place an order');
      navigate('/login');
      return;
    }

    setLoading(true);
    try {
      // Create order
      const orderData = {
        customerId: user.userId,
        items: items.map(item => ({
          productId: item.id,
          quantity: item.quantity,
        })),
        deliveryAddress: formData.address,
        deliveryCity: formData.city,
        deliveryState: formData.state,
        deliveryZip: formData.zipCode,
        deliveryInstructions: formData.instructions,
        scheduledDeliveryDate: formData.scheduledDate || null,
      };

      const orderResponse = await apiService.orders.create(orderData);

      if (orderResponse.success) {
        // Create payment intent
        const paymentResponse = await apiService.payments.createPaymentIntent(orderResponse.data.id);

        if (paymentResponse.success) {
          // Here you would integrate Stripe Elements
          // For now, we'll simulate successful payment
          await apiService.payments.confirm(
            orderResponse.data.id,
            true,
            'txn_' + Date.now()
          );

          // Clear cart and redirect
          clearCart();
          navigate('/order-success', {
            state: { order: orderResponse.data },
          });
        }
      }
    } catch (err: any) {
      alert('Order failed: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  if (items.length === 0) {
    navigate('/cart');
    return null;
  }

  return (
    <div className="checkout-page">
      <div className="container">
        <h1 className="page-title">Checkout</h1>

        <div className="checkout-steps">
          <div className={`step ${step >= 1 ? 'active' : ''}`}>
            <MapPin size={24} />
            <span>Delivery Address</span>
          </div>
          <div className={`step ${step >= 2 ? 'active' : ''}`}>
            <CreditCard size={24} />
            <span>Payment</span>
          </div>
          <div className={`step ${step >= 3 ? 'active' : ''}`}>
            <CheckCircle size={24} />
            <span>Confirm</span>
          </div>
        </div>

        <div className="checkout-content">
          <div className="checkout-form">
            {step === 1 && (
              <div className="form-section">
                <h2>Delivery Address</h2>
                <div className="form-row">
                  <div className="form-group">
                    <label>Street Address</label>
                    <input
                      type="text"
                      name="address"
                      value={formData.address}
                      onChange={handleInputChange}
                      placeholder="123 Main St"
                    />
                  </div>
                </div>

                <div className="form-row">
                  <div className="form-group">
                    <label>City</label>
                    <input
                      type="text"
                      name="city"
                      value={formData.city}
                      onChange={handleInputChange}
                      placeholder="New York"
                    />
                  </div>
                  <div className="form-group">
                    <label>State</label>
                    <input
                      type="text"
                      name="state"
                      value={formData.state}
                      onChange={handleInputChange}
                      placeholder="NY"
                    />
                  </div>
                  <div className="form-group">
                    <label>ZIP Code</label>
                    <input
                      type="text"
                      name="zipCode"
                      value={formData.zipCode}
                      onChange={handleInputChange}
                      placeholder="10001"
                    />
                  </div>
                </div>

                <div className="form-group">
                  <label>Delivery Instructions (Optional)</label>
                  <textarea
                    name="instructions"
                    value={formData.instructions}
                    onChange={handleInputChange}
                    placeholder="Gate code, floor, etc."
                    rows={3}
                  />
                </div>

                <div className="form-group">
                  <label>Scheduled Delivery Date (Optional)</label>
                  <input
                    type="datetime-local"
                    name="scheduledDate"
                    value={formData.scheduledDate}
                    onChange={handleInputChange}
                  />
                </div>

                <button
                  onClick={() => setStep(2)}
                  className="btn btn-primary btn-lg"
                >
                  Continue to Payment
                </button>
              </div>
            )}

            {step === 2 && (
              <div className="form-section">
                <h2>Payment Method</h2>
                <div className="payment-methods">
                  <div className="payment-method selected">
                    <CreditCard size={24} />
                    <div>
                      <h4>Credit/Debit Card</h4>
                      <p>Visa, Mastercard, American Express</p>
                    </div>
                  </div>
                  <div className="payment-method">
                    <DollarSign size={24} />
                    <div>
                      <h4>Cash on Delivery</h4>
                      <p>Pay when you receive your order</p>
                    </div>
                  </div>
                </div>

                {/* Stripe Card Element would go here */}
                <div className="card-details">
                  <div className="form-group">
                    <label>Card Number</label>
                    <input type="text" placeholder="1234 5678 9012 3456" />
                  </div>
                  <div className="form-row">
                    <div className="form-group">
                      <label>Expiry Date</label>
                      <input type="text" placeholder="MM/YY" />
                    </div>
                    <div className="form-group">
                      <label>CVV</label>
                      <input type="text" placeholder="123" />
                    </div>
                  </div>
                </div>

                <div className="checkout-actions">
                  <button
                    onClick={() => setStep(1)}
                    className="btn btn-outline"
                  >
                    Back
                  </button>
                  <button
                    onClick={() => setStep(3)}
                    className="btn btn-primary"
                  >
                    Review Order
                  </button>
                </div>
              </div>
            )}

            {step === 3 && (
              <div className="form-section">
                <h2>Order Review</h2>
                <div className="order-review">
                  <div className="review-section">
                    <h3>Delivery Address</h3>
                    <p>
                      {formData.address}<br />
                      {formData.city}, {formData.state} {formData.zipCode}
                    </p>
                  </div>

                  <div className="review-section">
                    <h3>Order Items</h3>
                    {items.map(item => (
                      <div key={item.id} className="review-item">
                        <span>{item.name} x {item.quantity}</span>
                        <span>${(item.price * item.quantity).toFixed(2)}</span>
                      </div>
                    ))}
                  </div>

                  <div className="review-section">
                    <h3>Payment Method</h3>
                    <p>Credit/Debit Card</p>
                  </div>
                </div>

                <div className="checkout-actions">
                  <button
                    onClick={() => setStep(2)}
                    className="btn btn-outline"
                  >
                    Back
                  </button>
                  <button
                    onClick={handlePlaceOrder}
                    className="btn btn-success btn-lg"
                    disabled={loading}
                  >
                    {loading ? (
                      <>
                        <Loader2 className="animate-spin" size={20} />
                        Processing...
                      </>
                    ) : (
                      <>
                        <CheckCircle size={20} />
                        Place Order - ${total.toFixed(2)}
                      </>
                    )}
                  </button>
                </div>
              </div>
            )}
          </div>

          <div className="order-summary card">
            <h2>Order Summary</h2>
            {items.map(item => (
              <div key={item.id} className="summary-item">
                <span>{item.name} x {item.quantity}</span>
                <span>${(item.price * item.quantity).toFixed(2)}</span>
              </div>
            ))}
            <hr />
            <div className="summary-row">
              <span>Subtotal</span>
              <span>${totalPrice.toFixed(2)}</span>
            </div>
            <div className="summary-row">
              <span>Delivery Fee</span>
              <span>{deliveryFee === 0 ? 'FREE' : `$${deliveryFee.toFixed(2)}`}</span>
            </div>
            <div className="summary-row">
              <span>Tax</span>
              <span>${tax.toFixed(2)}</span>
            </div>
            <hr />
            <div className="summary-row total">
              <span>Total</span>
              <span>${total.toFixed(2)}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Checkout;
