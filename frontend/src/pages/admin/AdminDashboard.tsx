import React, { useState, useEffect } from 'react';
import { apiService } from '../../../../services/apiService';
import { webSocketService } from '../../../../services/webSocketService';
import {
  DollarSign, ShoppingCart, Package, Users, TrendingUp, TrendingDown,
  AlertTriangle, Clock, CheckCircle, Truck
} from 'lucide-react';
import '../../../styles/AdminDashboard.css';

interface DashboardMetrics {
  totalRevenue: number;
  totalOrders: number;
  totalCustomers: number;
  totalProducts: number;
  pendingOrders: number;
  lowStockProducts: number;
}

const AdminDashboard: React.FC = () => {
  const [metrics, setMetrics] = useState<DashboardMetrics | null>(null);
  const [recentOrders, setRecentOrders] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadDashboardData();
    connectWebSocket();

    return () => {
      webSocketService.disconnect();
    };
  }, []);

  const loadDashboardData = async () => {
    try {
      const response = await apiService.analytics.getDashboard();
      if (response.success && response.data) {
        setMetrics(response.data.metrics || {
          totalRevenue: 0,
          totalOrders: 0,
          totalCustomers: 0,
          totalProducts: 0,
          pendingOrders: 0,
          lowStockProducts: 0,
        });
        setRecentOrders(response.data.recentOrders || []);
      }
    } catch (error) {
      console.error('Failed to load dashboard:', error);
      // Set default values
      setMetrics({
        totalRevenue: 0,
        totalOrders: 0,
        totalCustomers: 0,
        totalProducts: 0,
        pendingOrders: 0,
        lowStockProducts: 0,
      });
    } finally {
      setLoading(false);
    }
  };

  const connectWebSocket = () => {
    webSocketService.connect();

    // Subscribe to admin order updates
    webSocketService.subscribe('/topic/admin/orders', (message: any) => {
      console.log('New order update:', message);
      loadDashboardData();
    });

    // Subscribe to inventory alerts
    webSocketService.subscribe('/topic/admin/inventory', (message: any) => {
      console.log('Inventory alert:', message);
      loadDashboardData();
    });
  };

  const formatCurrency = (amount: number) => {
    return `$${amount.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
  };

  const getStatusIcon = (status: string) => {
    switch (status?.toUpperCase()) {
      case 'PENDING': return <Clock size={16} />;
      case 'PROCESSING': return <Truck size={16} />;
      case 'SHIPPED': return <Truck size={16} />;
      case 'DELIVERED': return <CheckCircle size={16} />;
      default: return <Clock size={16} />;
    }
  };

  if (loading) {
    return (
      <div className="admin-dashboard loading">
        <div className="loading-spinner">Loading dashboard...</div>
      </div>
    );
  }

  return (
    <div className="admin-dashboard">
      <div className="dashboard-header">
        <h1>Dashboard Overview</h1>
        <p className="subtitle">Real-time insights into your business performance</p>
      </div>

      {/* Metrics Cards */}
      <div className="metrics-grid">
        <div className="metric-card primary">
          <div className="metric-icon">
            <DollarSign size={28} />
          </div>
          <div className="metric-info">
            <div className="metric-value">{formatCurrency(metrics?.totalRevenue || 0)}</div>
            <div className="metric-label">Total Revenue</div>
            <div className="metric-trend positive">
              <TrendingUp size={14} />
              <span>+12.5% from last month</span>
            </div>
          </div>
        </div>

        <div className="metric-card">
          <div className="metric-icon">
            <ShoppingCart size={28} />
          </div>
          <div className="metric-info">
            <div className="metric-value">{metrics?.totalOrders || 0}</div>
            <div className="metric-label">Total Orders</div>
            <div className="metric-trend positive">
              <TrendingUp size={14} />
              <span>+8.2% from last month</span>
            </div>
          </div>
        </div>

        <div className="metric-card">
          <div className="metric-icon">
            <Users size={28} />
          </div>
          <div className="metric-info">
            <div className="metric-value">{metrics?.totalCustomers || 0}</div>
            <div className="metric-label">Total Customers</div>
            <div className="metric-trend positive">
              <TrendingUp size={14} />
              <span>+5.3% from last month</span>
            </div>
          </div>
        </div>

        <div className="metric-card">
          <div className="metric-icon">
            <Package size={28} />
          </div>
          <div className="metric-info">
            <div className="metric-value">{metrics?.totalProducts || 0}</div>
            <div className="metric-label">Total Products</div>
            <div className="metric-trend neutral">
              <span>Stable inventory</span>
            </div>
          </div>
        </div>

        <div className="metric-card warning">
          <div className="metric-icon">
            <AlertTriangle size={28} />
          </div>
          <div className="metric-info">
            <div className="metric-value">{metrics?.lowStockProducts || 0}</div>
            <div className="metric-label">Low Stock Products</div>
            <div className="metric-trend negative">
              <TrendingDown size={14} />
              <span>Needs attention</span>
            </div>
          </div>
        </div>

        <div className="metric-card info">
          <div className="metric-icon">
            <Clock size={28} />
          </div>
          <div className="metric-info">
            <div className="metric-value">{metrics?.pendingOrders || 0}</div>
            <div className="metric-label">Pending Orders</div>
            <div className="metric-trend neutral">
              <span>To be processed</span>
            </div>
          </div>
        </div>
      </div>

      {/* Recent Orders */}
      <div className="dashboard-section">
        <div className="section-header">
          <h2>Recent Orders</h2>
          <a href="/admin/orders" className="view-all">View All →</a>
        </div>
        <div className="recent-orders-table">
          <table>
            <thead>
              <tr>
                <th>Order #</th>
                <th>Customer</th>
                <th>Amount</th>
                <th>Status</th>
                <th>Date</th>
              </tr>
            </thead>
            <tbody>
              {recentOrders.length > 0 ? (
                recentOrders.map((order: any) => (
                  <tr key={order.id}>
                    <td className="order-number">#{order.orderNumber}</td>
                    <td>{order.customerName || 'N/A'}</td>
                    <td className="amount">${order.totalAmount?.toFixed(2)}</td>
                    <td>
                      <span className={`status-badge status-${order.status?.toLowerCase()}`}>
                        {getStatusIcon(order.status)}
                        {order.status}
                      </span>
                    </td>
                    <td className="date">
                      {new Date(order.createdAt).toLocaleDateString()}
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan={5} className="no-data">No recent orders</td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Quick Actions */}
      <div className="dashboard-section">
        <div className="section-header">
          <h2>Quick Actions</h2>
        </div>
        <div className="quick-actions">
          <a href="/admin/products" className="quick-action-card">
            <Package size={32} />
            <span>Add New Product</span>
          </a>
          <a href="/admin/inventory" className="quick-action-card">
            <AlertTriangle size={32} />
            <span>Check Low Stock</span>
          </a>
          <a href="/admin/orders" className="quick-action-card">
            <Clock size={32} />
            <span>Process Orders</span>
          </a>
          <a href="/admin/analytics" className="quick-action-card">
            <TrendingUp size={32} />
            <span>View Analytics</span>
          </a>
        </div>
      </div>
    </div>
  );
};

export default AdminDashboard;
