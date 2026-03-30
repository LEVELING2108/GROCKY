import React from 'react';
import { FileText, Download, Calendar, DollarSign } from 'lucide-react';
import '../../../styles/AdminCommon.css';

const Reports: React.FC = () => {
  const reports = [
    { id: 1, name: 'Sales Report', description: 'Detailed sales breakdown by product, category, and date range', icon: DollarSign },
    { id: 2, name: 'Inventory Report', description: 'Current stock levels, low stock alerts, and valuation', icon: FileText },
    { id: 3, name: 'Order Report', description: 'Order history with status, fulfillment times, and customer data', icon: Calendar },
    { id: 4, name: 'Customer Report', description: 'Customer acquisition, retention, and lifetime value', icon: FileText },
  ];

  return (
    <div className="admin-common-page">
      <div className="page-header">
        <div className="header-left">
          <h1>Reports</h1>
          <p>Generate and download business reports</p>
        </div>
      </div>

      <div className="reports-grid">
        {reports.map(report => {
          const Icon = report.icon;
          return (
            <div key={report.id} className="report-card">
              <div className="report-icon">
                <Icon size={40} color="#2ECC71" />
              </div>
              <h3>{report.name}</h3>
              <p>{report.description}</p>
              <div className="report-actions">
                <button className="btn btn-sm btn-primary">
                  <FileText size={16} /> Generate
                </button>
                <button className="btn btn-sm btn-secondary">
                  <Download size={16} /> Download
                </button>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
};

export default Reports;
