import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { apiService } from '../services/apiService';
import { webSocketService } from '../services/webSocketService';
import { Package, Truck, CheckCircle, Clock, AlertCircle, MapPin, Phone, Mail } from 'lucide-react';
import '../styles/OrderTracking.css';

const OrderTracking: React.FC = () => {
  const { orderId } = useParams<{ orderId: string }>();
  const navigate = useNavigate();
  const [order, setOrder] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [currentStatus, setCurrentStatus] = useState('');

  useEffect(() => {
    if (orderId) {
      fetchOrder(orderId);

      // Subscribe to real-time updates
      const unsubscribe = webSocketService.subscribeToOrder(orderId, (data) => {
        console.log('Order update received:', data);
        if (data.status) {
          setCurrentStatus(data.status);
          fetchOrder(orderId); // Refresh order data
        }
      });

      return () => {
        unsubscribe();
      };
    }
  }, [orderId]);

  const fetchOrder = async (id: string) => {
    try {
      const response = await apiService.orders.getById(id);
      if (response.success && response.data) {
        setOrder(response.data);
        setCurrentStatus(response.data.status);
      }
    } catch (err: any) {
      setError('Order not found');
    } finally {
      setLoading(false);
    }
  };

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'PENDING':
        return <Clock size={32} />;
      case 'CONFIRMED':
        return <CheckCircle size={32} />;
      case 'PROCESSING':
        return <Package size={32} />;
      case 'SHIPPED':
        return <Truck size={32} />;
      case 'DELIVERED':
        return <CheckCircle size={32} />;
      case 'CANCELLED':
        return <AlertCircle size={32} />;
      default:
        return <Clock size={32} />;
    }
  };

  const getStatusIndex = (status: string) => {
    const statuses = ['PENDING', 'CONFIRMED', 'PROCESSING', 'SHIPPED', 'DELIVERED'];
    return statuses.indexOf(status);
  };

  const timeline = [
    { status: 'PENDING', label: 'Order Placed', icon: Clock },
    { status: 'CONFIRMED', label: 'Order Confirmed', icon: CheckCircle },
    { status: 'PROCESSING', label: 'Processing', icon: Package },
    { status: 'SHIPPED', label: 'Out for Delivery', icon: Truck },
    { status: 'DELIVERED', label: 'Delivered', icon: CheckCircle },
  ];

  if (loading) {
    return (
      <div className="order-tracking-page">
        <div className="loading-container">
          <div className="spinner"></div>
          <p>Loading order details...</p>
        </div>
      </div>
    );
  }

  if (error || !order) {
    return (
      <div className="order-tracking-page">
        <div className="container">
          <div className="error-card card">
            <AlertCircle size={64} color="#E74C3C" />
            <h2>Order Not Found</h2>
            <p>{error || 'We could not find your order'}</p>
            <button onClick={() => navigate('/')} className="btn btn-primary">
              Back to Home
            </button>
          </div>
        </div>
      </div>
    );
  }

  const currentStatusIndex = getStatusIndex(order.status);

  return (
    <div className="order-tracking-page">
      <div className="container">
        <div className="tracking-header">
          <h1>Track Your Order</h1>
          <p className="order-number">Order #{order.orderNumber}</p>
        </div>

        <div className="tracking-content">
          <div className="order-status-card card">
            <div className="status-header">
              <div className="status-icon">
                {getStatusIcon(order.status)}
              </div>
              <div>
                <h2>Current Status: {order.status}</h2>
                <p>
                  {order.status === 'DELIVERED'
                    ? `Delivered on ${new Date(order.deliveredAt).toLocaleDateString()}`
                    : order.status === 'CANCELLED'
                    ? 'Your order has been cancelled'
                    : 'Your order is being processed'}
                </p>
              </div>
            </div>

            <div className="timeline">
              {timeline.map((step, index) => (
                <div
                  key={step.status}
                  className={`timeline-item ${
                    index <= currentStatusIndex ? 'completed' : ''
                  } ${index === currentStatusIndex ? 'current' : ''}`}
                >
                  <div className="timeline-icon">
                    <step.icon size={24} />
                  </div>
                  <div className="timeline-label">{step.label}</div>
                  {index < timeline.length - 1 && (
                    <div
                      className={`timeline-line ${
                        index < currentStatusIndex ? 'completed' : ''
                      }`}
                    />
                  )}
                </div>
              ))}
            </div>
          </div>

          <div className="order-details-grid">
            <div className="order-info card">
              <h3>Order Information</h3>
              <div className="info-row">
                <span>Order Number:</span>
                <strong>{order.orderNumber}</strong>
              </div>
              <div className="info-row">
                <span>Order Date:</span>
                <strong>{new Date(order.createdAt).toLocaleDateString()}</strong>
              </div>
              <div className="info-row">
                <span>Total Amount:</span>
                <strong>${order.totalAmount}</strong>
              </div>
              <div className="info-row">
                <span>Payment Status:</span>
                <strong className={order.paymentStatus === 'COMPLETED' ? 'success' : 'warning'}>
                  {order.paymentStatus}
                </strong>
              </div>
              {order.aiPredictedDeliveryTime && (
                <div className="info-row">
                  <span>Estimated Delivery:</span>
                  <strong>{order.aiPredictedDeliveryTime}</strong>
                </div>
              )}
            </div>

            <div className="delivery-info card">
              <h3>Delivery Address</h3>
              <div className="address">
                <MapPin size={20} color="#2ECC71" />
                <p>
                  {order.deliveryAddress}<br />
                  {order.deliveryCity}, {order.deliveryState} {order.deliveryZip}
                </p>
              </div>
              {order.deliveryInstructions && (
                <div className="instructions">
                  <strong>Instructions:</strong>
                  <p>{order.deliveryInstructions}</p>
                </div>
              )}
              {order.scheduledDeliveryDate && (
                <div className="scheduled">
                  <Clock size={20} color="#2ECC71" />
                  <p>Scheduled: {new Date(order.scheduledDeliveryDate).toLocaleString()}</p>
                </div>
              )}
            </div>
          </div>

          <div className="order-items card">
            <h3>Order Items</h3>
            <div className="items-list">
              {order.items?.map((item: any) => (
                <div key={item.id} className="item-row">
                  <img
                    src={item.productImage || 'https://via.placeholder.com/60'}
                    alt={item.productName}
                    className="item-image"
                  />
                  <div className="item-details">
                    <h4>{item.productName}</h4>
                    <p>Quantity: {item.quantity}</p>
                  </div>
                  <div className="item-price">
                    ${item.totalPrice?.toFixed(2)}
                  </div>
                </div>
              ))}
            </div>
          </div>

          {order.status !== 'DELIVERED' && order.status !== 'CANCELLED' && (
            <div className="tracking-actions">
              <button
                onClick={() => apiService.orders.cancel(order.id, 'Customer requested cancellation')}
                className="btn btn-outline"
              >
                Cancel Order
              </button>
              <button
                onClick={() => navigate('/contact', { state: { orderId: order.id } })}
                className="btn btn-primary"
              >
                Contact Support
              </button>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default OrderTracking;
