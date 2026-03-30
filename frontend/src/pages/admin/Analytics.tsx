import React, { useState, useEffect } from 'react';
import { apiService } from '../../../../services/apiService';
import { BarChart, LineChart, DollarSign, TrendingUp, Package, Users, ShoppingCart } from 'lucide-react';
import '../../../styles/Analytics.css';

const Analytics: React.FC = () => {
  const [loading, setLoading] = useState(true);
  const [dateRange, setDateRange] = useState('30');

  useEffect(() => {
    loadAnalytics();
  }, [dateRange]);

  const loadAnalytics = async () => {
    try {
      await apiService.analytics.getDashboard();
    } catch (error) {
      console.error('Failed to load analytics:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleExport = () => {
    alert('Export functionality - would generate CSV/PDF report');
  };

  if (loading) {
    return <div className="loading-container">Loading analytics...</div>;
  }

  return (
    <div className="analytics-page">
      <div className="page-header">
        <div className="header-left">
          <h1>Analytics & Reports</h1>
          <p>Detailed insights into your business performance</p>
        </div>
        <div className="header-actions">
          <select value={dateRange} onChange={(e) => setDateRange(e.target.value)} className="date-range-select">
            <option value="7">Last 7 Days</option>
            <option value="30">Last 30 Days</option>
            <option value="90">Last 90 Days</option>
            <option value="365">Last Year</option>
          </select>
          <button className="btn btn-primary" onClick={handleExport}>
            <TrendingUp size={18} />
            Export Report
          </button>
        </div>
      </div>

      {/* Key Metrics */}
      <div className="metrics-grid">
        <div className="metric-card">
          <div className="metric-icon"><DollarSign size={28} /></div>
          <div className="metric-info">
            <div className="metric-value">$12,458</div>
            <div className="metric-label">Total Revenue</div>
            <div className="metric-trend positive">+12.5% vs previous period</div>
          </div>
        </div>
        <div className="metric-card">
          <div className="metric-icon"><ShoppingCart size={28} /></div>
          <div className="metric-info">
            <div className="metric-value">342</div>
            <div className="metric-label">Total Orders</div>
            <div className="metric-trend positive">+8.2% vs previous period</div>
          </div>
        </div>
        <div className="metric-card">
          <div className="metric-icon"><Package size={28} /></div>
          <div className="metric-info">
            <div className="metric-value">48</div>
            <div className="metric-label">Products Sold</div>
            <div className="metric-trend positive">+15.3% vs previous period</div>
          </div>
        </div>
        <div className="metric-card">
          <div className="metric-icon"><Users size={28} /></div>
          <div className="metric-info">
            <div className="metric-value">156</div>
            <div className="metric-label">Active Customers</div>
            <div className="metric-trend positive">+5.1% vs previous period</div>
          </div>
        </div>
      </div>

      {/* Charts Placeholder */}
      <div className="charts-section">
        <div className="chart-card">
          <h2>Sales Trend</h2>
          <div className="chart-placeholder">
            <LineChart size={48} color="#ccc" />
            <p>Sales trend chart would be displayed here</p>
            <span>Showing data for the last {dateRange} days</span>
          </div>
        </div>

        <div className="chart-card">
          <h2>Revenue Breakdown</h2>
          <div className="chart-placeholder">
            <BarChart size={48} color="#ccc" />
            <p>Revenue by category chart would be displayed here</p>
          </div>
        </div>
      </div>

      {/* Top Products */}
      <div className="table-section">
        <h2>Top Selling Products</h2>
        <div className="table-container">
          <table className="data-table">
            <thead>
              <tr>
                <th>Product</th>
                <th>Category</th>
                <th>Units Sold</th>
                <th>Revenue</th>
                <th>Growth</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>Organic Bananas</td>
                <td>Produce</td>
                <td>156</td>
                <td>$467.44</td>
                <td className="positive">+12%</td>
              </tr>
              <tr>
                <td>Whole Milk</td>
                <td>Dairy</td>
                <td>128</td>
                <td>$446.72</td>
                <td className="positive">+8%</td>
              </tr>
              <tr>
                <td>Eggs - Large</td>
                <td>Dairy</td>
                <td>95</td>
                <td>$426.55</td>
                <td className="positive">+15%</td>
              </tr>
              <tr>
                <td>Chicken Breast</td>
                <td>Meat</td>
                <td>72</td>
                <td>$647.28</td>
                <td className="negative">-3%</td>
              </tr>
              <tr>
                <td>Bread - Whole Wheat</td>
                <td>Bakery</td>
                <td>68</td>
                <td>$189.72</td>
                <td className="positive">+5%</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default Analytics;
