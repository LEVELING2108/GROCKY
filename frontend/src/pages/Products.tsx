import React, { useState, useEffect } from 'react';
import Sidebar from '../components/Sidebar';
import ProductCard from '../components/ProductCard';
import { apiService } from '../services/apiService';
import '../styles/Products.css';

const Products: React.FC = () => {
  const [products, setProducts] = useState<any[]>([]);
  const [selectedCategory, setSelectedCategory] = useState('All');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchProducts = async () => {
      setLoading(true);
      try {
        // Demo data
        const demoData = [
          { id: 1, name: 'Organic Bananas', price: 1.99, category: 'Fruits' },
          { id: 2, name: 'Red Apples', price: 2.49, category: 'Fruits' },
          { id: 3, name: 'Fresh Spinach', price: 3.00, category: 'Vegetables' },
          { id: 4, name: 'Carrots 1kg', price: 1.50, category: 'Vegetables' },
          { id: 5, name: 'Whole Milk', price: 4.20, category: 'Dairy' },
          { id: 6, name: 'Greek Yogurt', price: 5.00, category: 'Dairy' },
          { id: 7, name: 'Sourdough Bread', price: 3.50, category: 'Bakery' },
          { id: 8, name: 'Chocolate Croissant', price: 2.75, category: 'Bakery' },
        ];
        
        setProducts(demoData);
        
        // Real API
        // const data = await apiService.get('/products');
        // setProducts(data);
      } catch (err) {
        console.error("Error loading products", err);
      } finally {
        setLoading(false);
      }
    };

    fetchProducts();
  }, []);

  const filteredProducts = selectedCategory === 'All' 
    ? products 
    : products.filter(p => p.category === selectedCategory);

  return (
    <div className="container products-page">
      <Sidebar 
        selectedCategory={selectedCategory} 
        setSelectedCategory={setSelectedCategory} 
      />
      
      <main className="products-main">
        <div className="products-header">
          <h2>{selectedCategory} Products</h2>
          <p>Showing {filteredProducts.length} results</p>
        </div>

        {loading ? (
          <div className="loading">Gathering fresh products...</div>
        ) : (
          <div className="products-grid">
            {filteredProducts.map(product => (
              <ProductCard key={product.id} product={product} />
            ))}
          </div>
        )}
      </main>
    </div>
  );
};

export default Products;
