import React from 'react';
import { Users, Mail, Phone, MapPin, Calendar, DollarSign, ShoppingCart } from 'lucide-react';
import '../../styles/AdminCommon.css';

const CustomerManagement: React.FC = () => {
  const customers = [
    { id: '1', name: 'John Doe', email: 'john@example.com', phone: '555-0101', orders: 5, spent: 250.50, joined: '2024-01-15' },
    { id: '2', name: 'Jane Smith', email: 'jane@example.com', phone: '555-0102', orders: 8, spent: 420.00, joined: '2024-02-20' },
    { id: '3', name: 'Bob Wilson', email: 'bob@example.com', phone: '555-0103', orders: 3, spent: 150.25, joined: '2024-03-10' },
  ];

  return (
    <div className="admin-common-page">
      <div className="page-header">
        <div className="header-left">
          <h1>Customer Management</h1>
          <p>View and manage all registered customers</p>
        </div>
      </div>

      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-value">{customers.length}</div>
          <div className="stat-label">Total Customers</div>
        </div>
        <div className="stat-card">
          <div className="stat-value">{customers.reduce((sum, c) => sum + c.orders, 0)}</div>
          <div className="stat-label">Total Orders</div>
        </div>
        <div className="stat-card">
          <div className="stat-value">${customers.reduce((sum, c) => sum + c.spent, 0).toFixed(2)}</div>
          <div className="stat-label">Total Revenue</div>
        </div>
      </div>

      <div className="table-container">
        <table className="data-table">
          <thead>
            <tr>
              <th>Customer</th>
              <th>Contact</th>
              <th>Orders</th>
              <th>Total Spent</th>
              <th>Joined</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {customers.map(customer => (
              <tr key={customer.id}>
                <td>
                  <div className="customer-cell">
                    <div className="avatar">{customer.name.charAt(0)}</div>
                    <strong>{customer.name}</strong>
                  </div>
                </td>
                <td>
                  <div className="contact-info">
                    <span><Mail size={14} /> {customer.email}</span>
                    <span><Phone size={14} /> {customer.phone}</span>
                  </div>
                </td>
                <td><ShoppingCart size={16} /> {customer.orders}</td>
                <td><DollarSign size={16} /> {customer.spent.toFixed(2)}</td>
                <td><Calendar size={16} /> {customer.joined}</td>
                <td>
                  <button className="btn btn-sm btn-primary">View Details</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default CustomerManagement;
