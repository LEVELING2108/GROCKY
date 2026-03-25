import React from 'react';
import { Plus, ShoppingCart } from 'lucide-react';
import { useCart } from '../context/CartContext';
import '../styles/ProductCard.css';

interface ProductProps {
  product: {
    id: number;
    name: string;
    price: number;
    category: string;
    imageUrl?: string;
    description?: string;
  };
}

const ProductCard: React.FC<ProductProps> = ({ product }) => {
  const { addToCart } = useCart();

  return (
    <div className="product-card fade-in">
      <div className="product-image">
        <img 
          src={product.imageUrl || `https://source.unsplash.com/400x400/?${product.name},grocery`} 
          alt={product.name} 
        />
        <div className="product-badge">{product.category}</div>
      </div>
      <div className="product-info">
        <h3 className="product-name">{product.name}</h3>
        <p className="product-desc">{product.description || 'Fresh and high quality product selected for you.'}</p>
        <div className="product-footer">
          <span className="product-price">${product.price.toFixed(2)}</span>
          <button 
            className="add-btn" 
            onClick={() => addToCart({ ...product, quantity: 1 })}
            title="Add to cart"
          >
            <Plus size={20} />
            <span>Add</span>
          </button>
        </div>
      </div>
    </div>
  );
};

export default ProductCard;
