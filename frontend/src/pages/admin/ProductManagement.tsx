import React, { useState, useEffect } from 'react';
import { apiService } from '@services/apiService';
import {
  Plus, Edit2, Trash2, Search, Filter, Package, DollarSign,
  AlertTriangle, CheckCircle, X, Save, Upload
} from 'lucide-react';
import '@styles/ProductManagement.css';

interface Product {
  id: string;
  name: string;
  description: string;
  category: string;
  subcategory?: string;
  price: number;
  costPrice?: number;
  stockQuantity: number;
  reorderLevel: number;
  unit: string;
  brand?: string;
  supplier?: string;
  imageUrl?: string;
  isAvailable: boolean;
  discountPercentage: number;
}

const ProductManagement: React.FC = () => {
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [editingProduct, setEditingProduct] = useState<Product | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [categoryFilter, setCategoryFilter] = useState('all');
  const [stockFilter, setStockFilter] = useState('all');

  const [formData, setFormData] = useState({
    name: '',
    description: '',
    category: '',
    subcategory: '',
    price: '',
    costPrice: '',
    stockQuantity: '',
    reorderLevel: '',
    unit: 'piece',
    brand: '',
    supplier: '',
    imageUrl: '',
    isAvailable: true,
    discountPercentage: '0',
  });

  const categories = [
    'Produce', 'Dairy', 'Bakery', 'Meat', 'Seafood',
    'Pantry', 'Beverages', 'Snacks', 'Frozen', 'Household'
  ];

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

  const handleOpenModal = (product?: Product) => {
    if (product) {
      setEditingProduct(product);
      setFormData({
        name: product.name,
        description: product.description,
        category: product.category,
        subcategory: product.subcategory || '',
        price: product.price.toString(),
        costPrice: product.costPrice?.toString() || '',
        stockQuantity: product.stockQuantity.toString(),
        reorderLevel: product.reorderLevel.toString(),
        unit: product.unit,
        brand: product.brand || '',
        supplier: product.supplier || '',
        imageUrl: product.imageUrl || '',
        isAvailable: product.isAvailable,
        discountPercentage: product.discountPercentage.toString(),
      });
    } else {
      setEditingProduct(null);
      setFormData({
        name: '',
        description: '',
        category: '',
        subcategory: '',
        price: '',
        costPrice: '',
        stockQuantity: '',
        reorderLevel: '',
        unit: 'piece',
        brand: '',
        supplier: '',
        imageUrl: '',
        isAvailable: true,
        discountPercentage: '0',
      });
    }
    setShowModal(true);
  };

  const handleCloseModal = () => {
    setShowModal(false);
    setEditingProduct(null);
  };

  const handleInputChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>
  ) => {
    const { name, value, type } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? (e.target as HTMLInputElement).checked : value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const productData = {
        ...formData,
        price: parseFloat(formData.price),
        costPrice: parseFloat(formData.costPrice) || undefined,
        stockQuantity: parseInt(formData.stockQuantity),
        reorderLevel: parseInt(formData.reorderLevel),
        discountPercentage: parseFloat(formData.discountPercentage),
      };

      if (editingProduct) {
        // Update existing product
        const response = await apiService.products.update(editingProduct.id, productData);
        if (response.success) {
          alert('Product updated successfully!');
          handleCloseModal();
          loadProducts();
        }
      } else {
        // Create new product
        const response = await apiService.products.create(productData);
        if (response.success) {
          alert('Product created successfully!');
          handleCloseModal();
          loadProducts();
        }
      }
    } catch (error: any) {
      alert(error.message || 'Failed to save product');
    }
  };

  const handleDelete = async (id: string) => {
    if (!confirm('Are you sure you want to delete this product?')) return;

    try {
      const response = await apiService.products.delete(id);
      if (response.success) {
        alert('Product deleted successfully!');
        loadProducts();
      }
    } catch (error: any) {
      alert(error.message || 'Failed to delete product');
    }
  };

  const filteredProducts = products.filter(product => {
    const matchesSearch = product.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      product.description.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesCategory = categoryFilter === 'all' || product.category === categoryFilter;
    const matchesStock = stockFilter === 'all' ||
      (stockFilter === 'low' && product.stockQuantity <= product.reorderLevel) ||
      (stockFilter === 'out' && product.stockQuantity === 0) ||
      (stockFilter === 'instock' && product.stockQuantity > product.reorderLevel);

    return matchesSearch && matchesCategory && matchesStock;
  });

  const getStockStatus = (product: Product) => {
    if (product.stockQuantity === 0) return { label: 'Out of Stock', class: 'out' };
    if (product.stockQuantity <= product.reorderLevel) return { label: 'Low Stock', class: 'low' };
    return { label: 'In Stock', class: 'in' };
  };

  const formatCurrency = (amount: number) => `$${amount.toFixed(2)}`;

  if (loading) {
    return <div className="loading-container">Loading products...</div>;
  }

  return (
    <div className="product-management">
      <div className="page-header">
        <div className="header-left">
          <h1>Product Management</h1>
          <p>Manage your product catalog and inventory</p>
        </div>
        <button className="btn btn-primary" onClick={() => handleOpenModal()}>
          <Plus size={20} />
          Add Product
        </button>
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

        <div className="filter-group">
          <div className="filter-select">
            <Filter size={16} />
            <select value={categoryFilter} onChange={(e) => setCategoryFilter(e.target.value)}>
              <option value="all">All Categories</option>
              {categories.map(cat => (
                <option key={cat} value={cat}>{cat}</option>
              ))}
            </select>
          </div>

          <div className="filter-select">
            <Package size={16} />
            <select value={stockFilter} onChange={(e) => setStockFilter(e.target.value)}>
              <option value="all">All Stock</option>
              <option value="instock">In Stock</option>
              <option value="low">Low Stock</option>
              <option value="out">Out of Stock</option>
            </select>
          </div>
        </div>
      </div>

      {/* Products Grid */}
      <div className="products-grid">
        {filteredProducts.length === 0 ? (
          <div className="no-products">
            <Package size={64} color="#ccc" />
            <h3>No products found</h3>
            <p>Add your first product to get started</p>
          </div>
        ) : (
          filteredProducts.map(product => {
            const stockStatus = getStockStatus(product);
            return (
              <div key={product.id} className="product-card">
                <div className="product-image">
                  {product.imageUrl ? (
                    <img src={product.imageUrl} alt={product.name} />
                  ) : (
                    <div className="no-image">
                      <Package size={40} color="#ccc" />
                    </div>
                  )}
                  <span className={`stock-badge ${stockStatus.class}`}>{stockStatus.label}</span>
                </div>

                <div className="product-info">
                  <h3 className="product-name">{product.name}</h3>
                  <p className="product-category">{product.category}</p>
                  <p className="product-description">{product.description}</p>

                  <div className="product-meta">
                    <div className="meta-item">
                      <DollarSign size={14} />
                      <span>{formatCurrency(product.price)}</span>
                    </div>
                    <div className="meta-item">
                      <Package size={14} />
                      <span>Stock: {product.stockQuantity}</span>
                    </div>
                    <div className="meta-item">
                      <span>Unit: {product.unit}</span>
                    </div>
                  </div>

                  {!product.isAvailable && (
                    <div className="unavailable-badge">
                      <AlertTriangle size={14} />
                      <span>Unavailable</span>
                    </div>
                  )}

                  <div className="product-actions">
                    <button
                      className="btn btn-sm btn-secondary"
                      onClick={() => handleOpenModal(product)}
                    >
                      <Edit2 size={16} />
                      Edit
                    </button>
                    <button
                      className="btn btn-sm btn-danger"
                      onClick={() => handleDelete(product.id)}
                    >
                      <Trash2 size={16} />
                      Delete
                    </button>
                  </div>
                </div>
              </div>
            );
          })
        )}
      </div>

      {/* Add/Edit Modal */}
      {showModal && (
        <div className="modal-overlay" onClick={handleCloseModal}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>{editingProduct ? 'Edit Product' : 'Add New Product'}</h2>
              <button className="close-btn" onClick={handleCloseModal}>
                <X size={24} />
              </button>
            </div>

            <form onSubmit={handleSubmit} className="product-form">
              <div className="form-section">
                <h3>Basic Information</h3>
                <div className="form-row">
                  <div className="form-group full-width">
                    <label>Product Name *</label>
                    <input
                      type="text"
                      name="name"
                      value={formData.name}
                      onChange={handleInputChange}
                      required
                    />
                  </div>
                </div>

                <div className="form-group full-width">
                  <label>Description</label>
                  <textarea
                    name="description"
                    value={formData.description}
                    onChange={handleInputChange}
                    rows={3}
                  />
                </div>

                <div className="form-row">
                  <div className="form-group">
                    <label>Category *</label>
                    <select
                      name="category"
                      value={formData.category}
                      onChange={handleInputChange}
                      required
                    >
                      <option value="">Select Category</option>
                      {categories.map(cat => (
                        <option key={cat} value={cat}>{cat}</option>
                      ))}
                    </select>
                  </div>

                  <div className="form-group">
                    <label>Subcategory</label>
                    <input
                      type="text"
                      name="subcategory"
                      value={formData.subcategory}
                      onChange={handleInputChange}
                    />
                  </div>
                </div>
              </div>

              <div className="form-section">
                <h3>Pricing & Inventory</h3>
                <div className="form-row">
                  <div className="form-group">
                    <label>Price ($) *</label>
                    <input
                      type="number"
                      name="price"
                      value={formData.price}
                      onChange={handleInputChange}
                      step="0.01"
                      min="0"
                      required
                    />
                  </div>

                  <div className="form-group">
                    <label>Cost Price ($)</label>
                    <input
                      type="number"
                      name="costPrice"
                      value={formData.costPrice}
                      onChange={handleInputChange}
                      step="0.01"
                      min="0"
                    />
                  </div>

                  <div className="form-group">
                    <label>Discount (%)</label>
                    <input
                      type="number"
                      name="discountPercentage"
                      value={formData.discountPercentage}
                      onChange={handleInputChange}
                      step="0.1"
                      min="0"
                      max="100"
                    />
                  </div>
                </div>

                <div className="form-row">
                  <div className="form-group">
                    <label>Stock Quantity *</label>
                    <input
                      type="number"
                      name="stockQuantity"
                      value={formData.stockQuantity}
                      onChange={handleInputChange}
                      min="0"
                      required
                    />
                  </div>

                  <div className="form-group">
                    <label>Reorder Level</label>
                    <input
                      type="number"
                      name="reorderLevel"
                      value={formData.reorderLevel}
                      onChange={handleInputChange}
                      min="0"
                    />
                  </div>

                  <div className="form-group">
                    <label>Unit</label>
                    <select name="unit" value={formData.unit} onChange={handleInputChange}>
                      <option value="piece">Piece</option>
                      <option value="kg">Kilogram</option>
                      <option value="g">Gram</option>
                      <option value="lb">Pound</option>
                      <option value="oz">Ounce</option>
                      <option value="liter">Liter</option>
                      <option value="ml">Milliliter</option>
                      <option value="dozen">Dozen</option>
                    </select>
                  </div>
                </div>
              </div>

              <div className="form-section">
                <h3>Additional Details</h3>
                <div className="form-row">
                  <div className="form-group">
                    <label>Brand</label>
                    <input
                      type="text"
                      name="brand"
                      value={formData.brand}
                      onChange={handleInputChange}
                    />
                  </div>

                  <div className="form-group">
                    <label>Supplier</label>
                    <input
                      type="text"
                      name="supplier"
                      value={formData.supplier}
                      onChange={handleInputChange}
                    />
                  </div>
                </div>

                <div className="form-group full-width">
                  <label>Image URL</label>
                  <input
                    type="url"
                    name="imageUrl"
                    value={formData.imageUrl}
                    onChange={handleInputChange}
                    placeholder="https://example.com/image.jpg"
                  />
                </div>

                <div className="form-group full-width">
                  <label className="checkbox-label">
                    <input
                      type="checkbox"
                      name="isAvailable"
                      checked={formData.isAvailable}
                      onChange={handleInputChange}
                    />
                    <span>Product is available for sale</span>
                  </label>
                </div>
              </div>

              <div className="form-actions">
                <button type="button" className="btn btn-secondary" onClick={handleCloseModal}>
                  Cancel
                </button>
                <button type="submit" className="btn btn-primary">
                  <Save size={18} />
                  {editingProduct ? 'Update Product' : 'Create Product'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default ProductManagement;
