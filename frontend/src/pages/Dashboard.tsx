import React, { useState, useEffect } from 'react';
import { apiService } from '../services/apiService';
import { webSocketService } from '../services/webSocketService';
import {
  DollarSign, ShoppingCart, Users, Package, TrendingUp, AlertTriangle,
  Truck, CheckCircle, Clock, BarChart3, Brain, RefreshCw
} from 'lucide-react';
import { Chart as ChartJS, CategoryScale, LinearScale, PointElement, LineElement, BarElement, ArcElement } from 'chart.js';
import { Line, Bar, Doughnut } from 'react-chartjs-2';
import '../styles/Dashboard.css';

ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, BarElement, ArcElement);

const Dashboard: React.FC = () => {
  const [metrics, setMetrics] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [newOrders, setNewOrders] = useState<any[]>([]);
  const [inventoryAlerts, setInventoryAlerts] = useState<any[]>([]);

  useEffect(() => {
    fetchDashboardMetrics();

    // Connect to WebSocket for real-time updates
    webSocketService.connect();

    // Subscribe to new orders
    const unsubscribeOrders = webSocketService.subscribeToNewOrders((data) => {
      console.log('New order received:', data);
      setNewOrders(prev => [data, ...prev].slice(0, 10));
      fetchDashboardMetrics(); // Refresh metrics
    });

    // Subscribe to inventory alerts
    const unsubscribeInventory = webSocketService.subscribeToInventory((data) => {
      console.log('Inventory alert:', data);
      setInventoryAlerts(prev => [data, ...prev].slice(0, 10));
    });

    // Subscribe to analytics updates
    const unsubscribeAnalytics = webSocketService.subscribeToAnalytics((data) => {
      console.log('Analytics update:', data);
      fetchDashboardMetrics();
    });

    return () => {
      unsubscribeOrders();
      unsubscribeInventory();
      unsubscribeAnalytics();
    };
  }, []);

  const fetchDashboardMetrics = async () => {
    try {
      const response = await apiService.analytics.getDashboard();
      if (response.success && response.data) {
        setMetrics(response.data);
      }
    } catch (err) {
      console.error('Failed to fetch dashboard metrics:', err);
    } finally {
      setLoading(false);
    }
  };

  const runAIForecasting = async () => {
    try {
      const response = await apiService.analytics.runForecasting();
      if (response.success) {
        alert('AI Forecasting completed successfully!');
        fetchDashboardMetrics();
      }
    } catch (err: any) {
      alert('AI Forecasting failed: ' + err.message);
    }
  };

  if (loading) {
    return (
      <div className="dashboard-page">
        <div className="loading-container">
          <div className="spinner"></div>
          <p>Loading dashboard...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="dashboard-page">
      <div className="dashboard-header">
        <div className="container">
          <h1>Admin Dashboard</h1>
          <button onClick={runAIForecasting} className="btn btn-primary">
            <Brain size={20} />
            Run AI Forecasting
          </button>
        </div>
      </div>

      <div className="container">
        {/* Metrics Cards */}
        <div className="metrics-grid">
          <div className="metric-card">
            <div className="metric-icon">
              <DollarSign size={32} color="#2ECC71" />
            </div>
            <div className="metric-info">
              <h3>Total Revenue</h3>
              <p className="metric-value">${metrics?.totalRevenue?.toFixed(2) || '0.00'}</p>
              <span className="metric-trend positive">
                <TrendingUp size={16} /> +12.5%
              </span>
            </div>
          </div>

          <div className="metric-card">
            <div className="metric-icon">
              <ShoppingCart size={32} color="#3498DB" />
            </div>
            <div className="metric-info">
              <h3>Total Orders</h3>
              <p className="metric-value">{metrics?.totalOrders || 0}</p>
              <span className="metric-trend positive">
                <TrendingUp size={16} /> +8.3%
              </span>
            </div>
          </div>

          <div className="metric-card">
            <div className="metric-icon">
              <Users size={32} color="#9B59B6" />
            </div>
            <div className="metric-info">
              <h3>Active Customers</h3>
              <p className="metric-value">{metrics?.totalCustomers || 0}</p>
              <span className="metric-trend positive">
                <TrendingUp size={16} /> +15.2%
              </span>
            </div>
          </div>

          <div className="metric-card">
            <div className="metric-icon">
              <Package size={32} color="#F39C12" />
            </div>
            <div className="metric-info">
              <h3>Products</h3>
              <p className="metric-value">{metrics?.totalProducts || 0}</p>
              <span className="metric-trend neutral">
                0%
              </span>
            </div>
          </div>

          <div className="metric-card">
            <div className="metric-icon">
              <Clock size={32} color="#E67E22" />
            </div>
            <div className="metric-info">
              <h3>Pending Orders</h3>
              <p className="metric-value">{metrics?.pendingOrders || 0}</p>
              <span className="metric-trend warning">
                Needs attention
              </span>
            </div>
          </div>

          <div className="metric-card">
            <div className="metric-icon">
              <AlertTriangle size={32} color="#E74C3C" />
            </div>
            <div className="metric-info">
              <h3>Low Stock Items</h3>
              <p className="metric-value">{metrics?.lowStockProducts || 0}</p>
              <span className="metric-trend negative">
                Reorder needed
              </span>
            </div>
          </div>
        </div>

        {/* Charts Section */}
        <div className="charts-section">
          <div className="chart-card card">
            <h3>Sales Trend (7 Days)</h3>
            {metrics?.salesTrend && (
              <Line
                data={{
                  labels: Object.keys(metrics.salesTrend),
                  datasets: [{
                    label: 'Revenue',
                    data: Object.values(metrics.salesTrend),
                    borderColor: '#2ECC71',
                    backgroundColor: 'rgba(46, 204, 113, 0.1)',
                    fill: true,
                    tension: 0.4,
                  }],
                }}
                options={{
                  responsive: true,
                  plugins: {
                    legend: { display: false },
                  },
                }}
              />
            )}
          </div>

          <div className="chart-card card">
            <h3>Orders Trend (7 Days)</h3>
            {metrics?.ordersTrend && (
              <Bar
                data={{
                  labels: Object.keys(metrics.ordersTrend),
                  datasets: [{
                    label: 'Orders',
                    data: Object.values(metrics.ordersTrend),
                    backgroundColor: '#3498DB',
                  }],
                }}
                options={{
                  responsive: true,
                  plugins: {
                    legend: { display: false },
                  },
                }}
              />
            )}
          </div>
        </div>

        {/* Top Products & Real-time Updates */}
        <div className="dashboard-grid">
          <div className="dashboard-card card">
            <div className="card-header">
              <h3>Top Selling Products</h3>
              <BarChart3 size={20} />
            </div>
            <div className="top-products">
              {metrics?.topProducts?.map((product: any, index: number) => (
                <div key={product.productId} className="product-row">
                  <span className="rank">#{index + 1}</span>
                  <span className="product-name">{product.productName}</span>
                  <span className="product-qty">{product.quantitySold} sold</span>
                </div>
              ))}
            </div>
          </div>

          <div className="dashboard-card card">
            <div className="card-header">
              <h3>Real-time Orders</h3>
              <RefreshCw size={20} />
            </div>
            <div className="realtime-orders">
              {newOrders.length === 0 ? (
                <p className="no-data">No new orders yet</p>
              ) : (
                newOrders.map((order, index) => (
                  <div key={index} className="order-notification">
                    <CheckCircle size={16} color="#2ECC71" />
                    <span>
                      Order #{order.orderNumber} - ${order.totalAmount}
                    </span>
                  </div>
                ))
              )}
            </div>
          </div>

          <div className="dashboard-card card">
            <div className="card-header">
              <h3>Inventory Alerts</h3>
              <AlertTriangle size={20} />
            </div>
            <div className="inventory-alerts">
              {inventoryAlerts.length === 0 ? (
                <p className="no-data">No inventory alerts</p>
              ) : (
                inventoryAlerts.map((alert, index) => (
                  <div key={index} className={`alert-notification ${alert.alertLevel === 'CRITICAL' ? 'critical' : 'warning'}`}>
                    <AlertTriangle size={16} />
                    <span>
                      {alert.productName} - Stock: {alert.currentStock}
                    </span>
                  </div>
                ))
              )}
            </div>
          </div>

          <div className="dashboard-card card">
            <div className="card-header">
              <h3>AI Insights</h3>
              <Brain size={20} />
            </div>
            <div className="ai-insights">
              <div className="insight-item">
                <TrendingUp size={20} color="#2ECC71" />
                <div>
                  <strong>Demand Prediction</strong>
                  <p>High demand expected for Dairy products this week</p>
                </div>
              </div>
              <div className="insight-item">
                <Truck size={20} color="#3498DB" />
                <div>
                  <strong>Delivery Optimization</strong>
                  <p>Average delivery time: {metrics?.averageOrderValue ? '2.5 days' : 'N/A'}</p>
                </div>
              </div>
              <div className="insight-item">
                <DollarSign size={20} color="#F39C12" />
                <div>
                  <strong>Revenue Forecast</strong>
                  <p>Projected monthly revenue: ${(metrics?.totalRevenue || 0) * 4}</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
