import React, { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import { apiService } from '../../services/apiService';
import { Package, Clock, CheckCircle, Truck, AlertCircle, ChevronRight, Calendar, DollarSign } from 'lucide-react';
import '../../styles/OrderHistory.css';

interface Order {
  id: string;
  orderNumber: string;
  status: string;
  totalAmount: number;
  createdAt: string;
  deliveredAt?: string;
  items?: OrderItem[];
}

interface OrderItem {
  id: string;
  productName: string;
  quantity: number;
  unitPrice: number;
  imageUrl?: string;
}

const OrderHistory: React.FC = () => {
  const { token } = useAuth();
  const [orders, setOrders] = useState<Order[]>([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState('all');
  const [expandedOrder, setExpandedOrder] = useState<string | null>(null);

  useEffect(() => {
    loadOrders();
  }, []);

  const loadOrders = async () => {
    try {
      const response = await apiService.orders.getByCustomer();
      if (response.success && response.data) {
        setOrders(response.data);
      }
    } catch (error) {
      console.error('Failed to load orders:', error);
    } finally {
      setLoading(false);
    }
  };

  const getStatusIcon = (status: string) => {
    switch (status.toUpperCase()) {
      case 'PENDING':
        return <Clock size={20} />;
      case 'CONFIRMED':
        return <CheckCircle size={20} />;
      case 'PROCESSING':
        return <Truck size={20} />;
      case 'SHIPPED':
        return <Truck size={20} />;
      case 'DELIVERED':
        return <CheckCircle size={20} />;
      case 'CANCELLED':
        return <AlertCircle size={20} />;
      default:
        return <Package size={20} />;
    }
  };

  const getStatusClass = (status: string) => {
    switch (status.toUpperCase()) {
      case 'PENDING':
        return 'status-pending';
      case 'CONFIRMED':
        return 'status-confirmed';
      case 'PROCESSING':
        return 'status-processing';
      case 'SHIPPED':
        return 'status-shipped';
      case 'DELIVERED':
        return 'status-delivered';
      case 'CANCELLED':
        return 'status-cancelled';
      default:
        return 'status-pending';
    }
  };

  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
    });
  };

  const formatCurrency = (amount: number) => {
    return `$${amount.toFixed(2)}`;
  };

  const filteredOrders = orders.filter(order => {
    if (filter === 'all') return true;
    return order.status.toUpperCase() === filter.toUpperCase();
  });

  const toggleExpand = (orderId: string) => {
    setExpandedOrder(expandedOrder === orderId ? null : orderId);
  };

  if (loading) {
    return (
      <div className="order-history-page">
        <div className="loading-container">
          <div className="spinner"></div>
          <p>Loading your orders...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="order-history-page">
      <div className="order-header">
        <h1>
          <Package size={32} />
          My Orders
        </h1>
        <p>Track and manage all your orders</p>
      </div>

      <div className="order-filters">
        <button
          className={`filter-btn ${filter === 'all' ? 'active' : ''}`}
          onClick={() => setFilter('all')}
        >
          All Orders
        </button>
        <button
          className={`filter-btn ${filter === 'pending' ? 'active' : ''}`}
          onClick={() => setFilter('PENDING')}
        >
          Pending
        </button>
        <button
          className={`filter-btn ${filter === 'processing' ? 'active' : ''}`}
          onClick={() => setFilter('PROCESSING')}
        >
          Processing
        </button>
        <button
          className={`filter-btn ${filter === 'delivered' ? 'active' : ''}`}
          onClick={() => setFilter('DELIVERED')}
        >
          Delivered
        </button>
        <button
          className={`filter-btn ${filter === 'cancelled' ? 'active' : ''}`}
          onClick={() => setFilter('CANCELLED')}
        >
          Cancelled
        </button>
      </div>

      <div className="orders-list">
        {filteredOrders.length === 0 ? (
          <div className="no-orders">
            <Package size={64} color="#ccc" />
            <h3>No orders found</h3>
            <p>Start shopping to see your orders here!</p>
            <a href="/products" className="btn btn-primary">Browse Products</a>
          </div>
        ) : (
          filteredOrders.map(order => (
            <div key={order.id} className="order-card">
              <div className="order-header-row" onClick={() => toggleExpand(order.id)}>
                <div className="order-info">
                  <div className="order-number">
                    <strong>#{order.orderNumber}</strong>
                  </div>
                  <div className="order-date">
                    <Calendar size={14} />
                    <span>{formatDate(order.createdAt)}</span>
                  </div>
                </div>
                <div className="order-status">
                  <span className={`status-badge ${getStatusClass(order.status)}`}>
                    {getStatusIcon(order.status)}
                    <span>{order.status}</span>
                  </span>
                </div>
                <div className="order-total">
                  <DollarSign size={18} />
                  <span>{formatCurrency(order.totalAmount)}</span>
                </div>
                <div className="order-expand">
                  <ChevronRight
                    size={24}
                    className={expandedOrder === order.id ? 'expanded' : ''}
                  />
                </div>
              </div>

              {expandedOrder === order.id && (
                <div className="order-details">
                  <div className="details-section">
                    <h4>Order Items</h4>
                    <div className="order-items">
                      {order.items?.map(item => (
                        <div key={item.id} className="order-item">
                          <div className="item-image">
                            {item.imageUrl ? (
                              <img src={item.imageUrl} alt={item.productName} />
                            ) : (
                              <Package size={40} color="#ccc" />
                            )}
                          </div>
                          <div className="item-details">
                            <div className="item-name">{item.productName}</div>
                            <div className="item-meta">
                              Qty: {item.quantity} × {formatCurrency(item.unitPrice)}
                            </div>
                          </div>
                          <div className="item-total">
                            {formatCurrency(item.quantity * item.unitPrice)}
                          </div>
                        </div>
                      ))}
                    </div>
                  </div>

                  <div className="details-section">
                    <h4>Order Summary</h4>
                    <div className="order-summary">
                      <div className="summary-row">
                        <span>Subtotal</span>
                        <span>{formatCurrency(order.totalAmount * 0.9)}</span>
                      </div>
                      <div className="summary-row">
                        <span>Tax</span>
                        <span>{formatCurrency(order.totalAmount * 0.1)}</span>
                      </div>
                      <div className="summary-row total">
                        <span>Total</span>
                        <span>{formatCurrency(order.totalAmount)}</span>
                      </div>
                    </div>
                  </div>

                  <div className="order-actions">
                    <a href={`/orders/${order.id}`} className="btn btn-primary">
                      Track Order
                    </a>
                    {order.status.toUpperCase() === 'PENDING' && (
                      <button className="btn btn-danger">Cancel Order</button>
                    )}
                    {order.status.toUpperCase() === 'DELIVERED' && (
                      <button className="btn btn-secondary">Write Review</button>
                    )}
                  </div>
                </div>
              )}
            </div>
          ))
        )}
      </div>
    </div>
  );
};

export default OrderHistory;
