import React, { useState, useEffect } from 'react';
import { apiService } from '@services/apiService';
import { webSocketService } from '@services/webSocketService';
import { Search, Filter, Eye, CheckCircle, Truck, Package, Clock, X, Calendar } from 'lucide-react';
import '@styles/OrderFulfillment.css';

interface Order {
  id: string;
  orderNumber: string;
  customerName?: string;
  customerEmail?: string;
  status: string;
  totalAmount: number;
  createdAt: string;
  deliveryAddress?: string;
  deliveryCity?: string;
  deliveryState?: string;
  deliveryZip?: string;
  items?: OrderItem[];
}

interface OrderItem {
  productName: string;
  quantity: number;
  unitPrice: number;
}

const OrderFulfillment: React.FC = () => {
  const [orders, setOrders] = useState<Order[]>([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState('all');
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedOrder, setSelectedOrder] = useState<Order | null>(null);
  const [showModal, setShowModal] = useState(false);

  useEffect(() => {
    loadOrders();
    connectWebSocket();
    return () => webSocketService.disconnect();
  }, []);

  const loadOrders = async () => {
    try {
      const response = await apiService.orders.getAll();
      if (response.success && response.data) {
        setOrders(response.data);
      }
    } catch (error) {
      console.error('Failed to load orders:', error);
    } finally {
      setLoading(false);
    }
  };

  const connectWebSocket = () => {
    webSocketService.connect();
    webSocketService.subscribe('/topic/admin/new-orders', () => {
      loadOrders();
    });
  };

  const handleStatusUpdate = async (orderId: string, newStatus: string) => {
    try {
      const response = await apiService.orders.updateStatus(orderId, newStatus);
      if (response.success) {
        alert('Order status updated!');
        loadOrders();
        if (selectedOrder?.id === orderId) {
          setSelectedOrder({ ...selectedOrder, status: newStatus });
        }
      }
    } catch (error: any) {
      alert(error.message || 'Failed to update status');
    }
  };

  const openOrderDetails = (order: Order) => {
    setSelectedOrder(order);
    setShowModal(true);
  };

  const filteredOrders = orders.filter(order => {
    const matchesSearch = order.orderNumber.toLowerCase().includes(searchTerm.toLowerCase()) ||
      order.customerName?.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesFilter = filter === 'all' || order.status.toUpperCase() === filter.toUpperCase();
    return matchesSearch && matchesFilter;
  });

  const getStatusIcon = (status: string) => {
    switch (status?.toUpperCase()) {
      case 'PENDING': return <Clock size={16} />;
      case 'CONFIRMED': return <CheckCircle size={16} />;
      case 'PROCESSING': return <Package size={16} />;
      case 'SHIPPED': return <Truck size={16} />;
      case 'DELIVERED': return <CheckCircle size={16} />;
      case 'CANCELLED': return <X size={16} />;
      default: return <Clock size={16} />;
    }
  };

  const getStatusClass = (status: string) => `status-${status?.toLowerCase()}`;

  const formatCurrency = (amount: number) => `$${amount.toFixed(2)}`;

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('en-US', {
      year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit'
    });
  };

  const stats = {
    total: orders.length,
    pending: orders.filter(o => o.status.toUpperCase() === 'PENDING').length,
    processing: orders.filter(o => o.status.toUpperCase() === 'PROCESSING').length,
    delivered: orders.filter(o => o.status.toUpperCase() === 'DELIVERED').length,
  };

  if (loading) {
    return <div className="loading-container">Loading orders...</div>;
  }

  return (
    <div className="order-fulfillment">
      <div className="page-header">
        <div className="header-left">
          <h1>Order Fulfillment</h1>
          <p>Manage and track all customer orders</p>
        </div>
      </div>

      {/* Stats */}
      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-value">{stats.total}</div>
          <div className="stat-label">Total Orders</div>
        </div>
        <div className="stat-card pending">
          <div className="stat-value">{stats.pending}</div>
          <div className="stat-label">Pending</div>
        </div>
        <div className="stat-card processing">
          <div className="stat-value">{stats.processing}</div>
          <div className="stat-label">Processing</div>
        </div>
        <div className="stat-card delivered">
          <div className="stat-value">{stats.delivered}</div>
          <div className="stat-label">Delivered</div>
        </div>
      </div>

      {/* Filters */}
      <div className="filters-bar">
        <div className="search-box">
          <Search size={18} />
          <input
            type="text"
            placeholder="Search by order # or customer..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>
        <div className="filter-buttons">
          {['all', 'PENDING', 'CONFIRMED', 'PROCESSING', 'SHIPPED', 'DELIVERED', 'CANCELLED'].map(status => (
            <button
              key={status}
              className={`filter-btn ${filter === status ? 'active' : ''}`}
              onClick={() => setFilter(status)}
            >
              {status === 'all' ? 'All' : status}
            </button>
          ))}
        </div>
      </div>

      {/* Orders Table */}
      <div className="orders-table-container">
        <table className="orders-table">
          <thead>
            <tr>
              <th>Order #</th>
              <th>Customer</th>
              <th>Amount</th>
              <th>Items</th>
              <th>Status</th>
              <th>Date</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {filteredOrders.length > 0 ? (
              filteredOrders.map(order => (
                <tr key={order.id}>
                  <td className="order-number">#{order.orderNumber}</td>
                  <td>
                    <div className="customer-info">
                      <div className="customer-name">{order.customerName || 'N/A'}</div>
                      <div className="customer-email">{order.customerEmail || ''}</div>
                    </div>
                  </td>
                  <td className="amount">{formatCurrency(order.totalAmount)}</td>
                  <td>{order.items?.length || 0} items</td>
                  <td>
                    <span className={`status-badge ${getStatusClass(order.status)}`}>
                      {getStatusIcon(order.status)}
                      {order.status}
                    </span>
                  </td>
                  <td className="date">{formatDate(order.createdAt)}</td>
                  <td>
                    <button className="btn btn-sm btn-primary" onClick={() => openOrderDetails(order)}>
                      <Eye size={16} />
                      View
                    </button>
                  </td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan={7} className="no-data">No orders found</td>
              </tr>
            )}
          </tbody>
        </table>
      </div>

      {/* Order Details Modal */}
      {showModal && selectedOrder && (
        <div className="modal-overlay" onClick={() => setShowModal(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>Order #{selectedOrder.orderNumber}</h2>
              <button className="close-btn" onClick={() => setShowModal(false)}>
                <X size={24} />
              </button>
            </div>

            <div className="modal-body">
              <div className="order-info-section">
                <div className="info-row">
                  <span className="label">Status:</span>
                  <span className={`status-badge ${getStatusClass(selectedOrder.status)}`}>
                    {getStatusIcon(selectedOrder.status)}
                    {selectedOrder.status}
                  </span>
                </div>
                <div className="info-row">
                  <span className="label">Order Date:</span>
                  <span><Calendar size={14} /> {formatDate(selectedOrder.createdAt)}</span>
                </div>
                <div className="info-row">
                  <span className="label">Customer:</span>
                  <span>{selectedOrder.customerName}</span>
                </div>
                <div className="info-row">
                  <span className="label">Email:</span>
                  <span>{selectedOrder.customerEmail}</span>
                </div>
                {selectedOrder.deliveryAddress && (
                  <div className="info-row">
                    <span className="label">Delivery Address:</span>
                    <span>{selectedOrder.deliveryAddress}, {selectedOrder.deliveryCity}, {selectedOrder.deliveryState} {selectedOrder.deliveryZip}</span>
                  </div>
                )}
              </div>

              <div className="order-items-section">
                <h3>Order Items</h3>
                <div className="items-list">
                  {selectedOrder.items?.map((item, index) => (
                    <div key={index} className="item-row">
                      <span className="item-name">{item.productName}</span>
                      <span className="item-qty">Qty: {item.quantity}</span>
                      <span className="item-price">{formatCurrency(item.unitPrice * item.quantity)}</span>
                    </div>
                  ))}
                </div>
                <div className="order-total">
                  <span>Total:</span>
                  <span className="amount">{formatCurrency(selectedOrder.totalAmount)}</span>
                </div>
              </div>

              <div className="status-actions">
                <h3>Update Status</h3>
                <div className="action-buttons">
                  {selectedOrder.status.toUpperCase() === 'PENDING' && (
                    <>
                      <button className="btn btn-secondary" onClick={() => handleStatusUpdate(selectedOrder.id, 'CONFIRMED')}>
                        <CheckCircle size={16} /> Confirm
                      </button>
                      <button className="btn btn-danger" onClick={() => handleStatusUpdate(selectedOrder.id, 'CANCELLED')}>
                        <X size={16} /> Cancel
                      </button>
                    </>
                  )}
                  {selectedOrder.status.toUpperCase() === 'CONFIRMED' && (
                    <button className="btn btn-primary" onClick={() => handleStatusUpdate(selectedOrder.id, 'PROCESSING')}>
                      <Package size={16} /> Mark as Processing
                    </button>
                  )}
                  {selectedOrder.status.toUpperCase() === 'PROCESSING' && (
                    <button className="btn btn-primary" onClick={() => handleStatusUpdate(selectedOrder.id, 'SHIPPED')}>
                      <Truck size={16} /> Mark as Shipped
                    </button>
                  )}
                  {selectedOrder.status.toUpperCase() === 'SHIPPED' && (
                    <button className="btn btn-success" onClick={() => handleStatusUpdate(selectedOrder.id, 'DELIVERED')}>
                      <CheckCircle size={16} /> Mark as Delivered
                    </button>
                  )}
                </div>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default OrderFulfillment;
