import React, { useState, useEffect } from 'react';
import { apiService } from '../../../../services/apiService';
import {
  AlertTriangle, Package, Search, Filter, Plus, Minus, Save,
  TrendingUp, TrendingDown, CheckCircle, X
} from 'lucide-react';
import '../../../styles/InventoryManagement.css';

interface Product {
  id: string;
  name: string;
  category: string;
  price: number;
  stockQuantity: number;
  reorderLevel: number;
  supplier?: string;
  isAvailable: boolean;
}

const InventoryManagement: React.FC = () => {
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [filterType, setFilterType] = useState('all');
  const [adjustmentModal, setAdjustmentModal] = useState<{ open: boolean; productId: string | null; type: 'add' | 'remove' | 'set' }>({ open: false, productId: null, type: 'add' });
  const [adjustmentValue, setAdjustmentValue] = useState('');

  useEffect(() => {
    loadProducts();
  }, []);

  const loadProducts = async () => {
    try {
      const response = await apiService.products.getAll();
      if (response.success && response.data) {
        setProducts(response.data);
      }
    } catch (error) {
      console.error('Failed to load products:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleAdjustStock = async () => {
    if (!adjustmentModal.productId || !adjustmentValue) return;

    try {
      const product = products.find(p => p.id === adjustmentModal.productId);
      if (!product) return;

      let newQuantity = product.stockQuantity;
      if (adjustmentModal.type === 'add') {
        newQuantity += parseInt(adjustmentValue);
      } else if (adjustmentModal.type === 'remove') {
        newQuantity -= parseInt(adjustmentValue);
      } else {
        newQuantity = parseInt(adjustmentValue);
      }

      if (newQuantity < 0) {
        alert('Stock quantity cannot be negative');
        return;
      }

      const response = await apiService.products.update(adjustmentModal.productId, {
        stockQuantity: newQuantity
      });

      if (response.success) {
        alert('Stock updated successfully!');
        setAdjustmentModal({ open: false, productId: null, type: 'add' });
        setAdjustmentValue('');
        loadProducts();
      }
    } catch (error: any) {
      alert(error.message || 'Failed to update stock');
    }
  };

  const openAdjustmentModal = (productId: string, type: 'add' | 'remove' | 'set') => {
    setAdjustmentModal({ open: true, productId, type });
    setAdjustmentValue('');
  };

  const filteredProducts = products.filter(product => {
    const matchesSearch = product.name.toLowerCase().includes(searchTerm.toLowerCase());
    
    if (filterType === 'all') return matchesSearch;
    if (filterType === 'low') return matchesSearch && product.stockQuantity <= product.reorderLevel && product.stockQuantity > 0;
    if (filterType === 'out') return matchesSearch && product.stockQuantity === 0;
    if (filterType === 'critical') return matchesSearch && product.stockQuantity <= 5;
    return matchesSearch;
  });

  const getStockStatus = (product: Product) => {
    if (product.stockQuantity === 0) return { label: 'Out of Stock', class: 'out', icon: AlertTriangle };
    if (product.stockQuantity <= 5) return { label: 'Critical', class: 'critical', icon: AlertTriangle };
    if (product.stockQuantity <= product.reorderLevel) return { label: 'Low Stock', class: 'low', icon: AlertTriangle };
    return { label: 'In Stock', class: 'in', icon: CheckCircle };
  };

  const stats = {
    total: products.length,
    inStock: products.filter(p => p.stockQuantity > p.reorderLevel).length,
    lowStock: products.filter(p => p.stockQuantity <= p.reorderLevel && p.stockQuantity > 0).length,
    outOfStock: products.filter(p => p.stockQuantity === 0).length,
  };

  if (loading) {
    return <div className="loading-container">Loading inventory...</div>;
  }

  return (
    <div className="inventory-management">
      <div className="page-header">
        <div className="header-left">
          <h1>Inventory Management</h1>
          <p>Monitor and adjust stock levels across your warehouse</p>
        </div>
      </div>

      {/* Stats Cards */}
      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-icon total">
            <Package size={24} />
          </div>
          <div className="stat-info">
            <div className="stat-value">{stats.total}</div>
            <div className="stat-label">Total Products</div>
          </div>
        </div>

        <div className="stat-card in-stock">
          <div className="stat-icon">
            <CheckCircle size={24} />
          </div>
          <div className="stat-info">
            <div className="stat-value">{stats.inStock}</div>
            <div className="stat-label">In Stock</div>
          </div>
        </div>

        <div className="stat-card low-stock">
          <div className="stat-icon">
            <AlertTriangle size={24} />
          </div>
          <div className="stat-info">
            <div className="stat-value">{stats.lowStock}</div>
            <div className="stat-label">Low Stock</div>
          </div>
        </div>

        <div className="stat-card out-of-stock">
          <div className="stat-icon">
            <X size={24} />
          </div>
          <div className="stat-info">
            <div className="stat-value">{stats.outOfStock}</div>
            <div className="stat-label">Out of Stock</div>
          </div>
        </div>
      </div>

      {/* Filters */}
      <div className="filters-bar">
        <div className="search-box">
          <Search size={18} />
          <input
            type="text"
            placeholder="Search products..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>

        <div className="filter-buttons">
          <button
            className={`filter-btn ${filterType === 'all' ? 'active' : ''}`}
            onClick={() => setFilterType('all')}
          >
            All ({stats.total})
          </button>
          <button
            className={`filter-btn ${filterType === 'low' ? 'active' : ''}`}
            onClick={() => setFilterType('low')}
          >
            Low Stock ({stats.lowStock})
          </button>
          <button
            className={`filter-btn ${filterType === 'out' ? 'active' : ''}`}
            onClick={() => setFilterType('out')}
          >
            Out of Stock ({stats.outOfStock})
          </button>
          <button
            className={`filter-btn ${filterType === 'critical' ? 'active' : ''}`}
            onClick={() => setFilterType('critical')}
          >
            Critical ({products.filter(p => p.stockQuantity <= 5).length})
          </button>
        </div>
      </div>

      {/* Inventory Table */}
      <div className="inventory-table-container">
        <table className="inventory-table">
          <thead>
            <tr>
              <th>Product</th>
              <th>Category</th>
              <th>Supplier</th>
              <th>Price</th>
              <th>Stock Level</th>
              <th>Reorder Point</th>
              <th>Status</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {filteredProducts.map(product => {
              const stockStatus = getStockStatus(product);
              const StatusIcon = stockStatus.icon;
              
              return (
                <tr key={product.id} className={stockStatus.class}>
                  <td className="product-name">
                    <strong>{product.name}</strong>
                  </td>
                  <td>{product.category}</td>
                  <td>{product.supplier || '-'}</td>
                  <td className="price">${product.price.toFixed(2)}</td>
                  <td>
                    <div className="stock-level">
                      <span className="quantity">{product.stockQuantity}</span>
                      <div className="stock-bar">
                        <div
                          className={`stock-fill ${stockStatus.class}`}
                          style={{ width: `${Math.min(100, (product.stockQuantity / (product.reorderLevel * 3)) * 100)}%` }}
                        />
                      </div>
                    </div>
                  </td>
                  <td>{product.reorderLevel}</td>
                  <td>
                    <span className={`status-badge ${stockStatus.class}`}>
                      <StatusIcon size={14} />
                      {stockStatus.label}
                    </span>
                  </td>
                  <td>
                    <div className="action-buttons">
                      <button
                        className="btn-icon"
                        onClick={() => openAdjustmentModal(product.id, 'add')}
                        title="Add Stock"
                      >
                        <Plus size={16} />
                      </button>
                      <button
                        className="btn-icon"
                        onClick={() => openAdjustmentModal(product.id, 'remove')}
                        title="Remove Stock"
                      >
                        <Minus size={16} />
                      </button>
                      <button
                        className="btn-icon"
                        onClick={() => openAdjustmentModal(product.id, 'set')}
                        title="Set Stock"
                      >
                        <Save size={16} />
                      </button>
                    </div>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>

      {/* Stock Adjustment Modal */}
      {adjustmentModal.open && (
        <div className="modal-overlay" onClick={() => setAdjustmentModal({ open: false, productId: null, type: 'add' })}>
          <div className="modal-content small" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>
                {adjustmentModal.type === 'add' && 'Add Stock'}
                {adjustmentModal.type === 'remove' && 'Remove Stock'}
                {adjustmentModal.type === 'set' && 'Set Stock Level'}
              </h2>
              <button className="close-btn" onClick={() => setAdjustmentModal({ open: false, productId: null, type: 'add' })}>
                <X size={24} />
              </button>
            </div>

            <div className="modal-body">
              <div className="form-group">
                <label>
                  {adjustmentModal.type === 'add' && 'Quantity to Add'}
                  {adjustmentModal.type === 'remove' && 'Quantity to Remove'}
                  {adjustmentModal.type === 'set' && 'New Stock Level'}
                </label>
                <input
                  type="number"
                  value={adjustmentValue}
                  onChange={(e) => setAdjustmentValue(e.target.value)}
                  min="0"
                  autoFocus
                />
              </div>

              <div className="modal-actions">
                <button className="btn btn-secondary" onClick={() => setAdjustmentModal({ open: false, productId: null, type: 'add' })}>
                  Cancel
                </button>
                <button className="btn btn-primary" onClick={handleAdjustStock}>
                  <Save size={18} />
                  Update Stock
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default InventoryManagement;
