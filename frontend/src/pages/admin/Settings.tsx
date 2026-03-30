import React from 'react';
import { Settings as SettingsIcon, Bell, Lock, Store, Mail } from 'lucide-react';
import '../../../styles/AdminCommon.css';

const Settings: React.FC = () => {
  return (
    <div className="admin-common-page">
      <div className="page-header">
        <div className="header-left">
          <h1>Settings</h1>
          <p>Configure your store settings and preferences</p>
        </div>
      </div>

      <div className="settings-sections">
        <div className="settings-card">
          <div className="settings-header">
            <Store size={24} />
            <h2>Store Settings</h2>
          </div>
          <div className="settings-form">
            <div className="form-group">
              <label>Store Name</label>
              <input type="text" defaultValue="GROCKY" />
            </div>
            <div className="form-group">
              <label>Store Email</label>
              <input type="email" defaultValue="hello@grocky.com" />
            </div>
            <div className="form-group">
              <label>Store Phone</label>
              <input type="tel" defaultValue="+1 (234) 567-890" />
            </div>
            <div className="form-group full-width">
              <label>Store Address</label>
              <input type="text" defaultValue="123 Fresh St, Farm City" />
            </div>
            <button className="btn btn-primary">Save Store Settings</button>
          </div>
        </div>

        <div className="settings-card">
          <div className="settings-header">
            <Bell size={24} />
            <h2>Notification Settings</h2>
          </div>
          <div className="settings-form">
            <div className="checkbox-group">
              <label className="checkbox-label">
                <input type="checkbox" defaultChecked />
                <span>Email notifications for new orders</span>
              </label>
              <label className="checkbox-label">
                <input type="checkbox" defaultChecked />
                <span>Email notifications for low stock alerts</span>
              </label>
              <label className="checkbox-label">
                <input type="checkbox" />
                <span>Daily sales summary email</span>
              </label>
            </div>
            <button className="btn btn-primary">Save Notification Settings</button>
          </div>
        </div>

        <div className="settings-card">
          <div className="settings-header">
            <Lock size={24} />
            <h2>Security Settings</h2>
          </div>
          <div className="settings-form">
            <div className="form-group">
              <label>Current Password</label>
              <input type="password" />
            </div>
            <div className="form-group">
              <label>New Password</label>
              <input type="password" />
            </div>
            <div className="form-group">
              <label>Confirm New Password</label>
              <input type="password" />
            </div>
            <button className="btn btn-primary">Change Password</button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Settings;
