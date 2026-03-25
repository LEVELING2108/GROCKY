import React from 'react';
import { Filter, ChevronRight } from 'lucide-react';
import '../styles/Sidebar.css';

interface SidebarProps {
  selectedCategory: string;
  setSelectedCategory: (category: string) => void;
}

const categories = [
  { name: 'All', icon: '🛒' },
  { name: 'Fruits', icon: '🍎' },
  { name: 'Vegetables', icon: '🥬' },
  { name: 'Dairy', icon: '🥛' },
  { name: 'Bakery', icon: '🍞' },
  { name: 'Meat', icon: '🥩' },
  { name: 'Snacks', icon: '🍿' },
  { name: 'Beverages', icon: '🥤' }
];

const Sidebar: React.FC<SidebarProps> = ({ selectedCategory, setSelectedCategory }) => {
  return (
    <aside className="sidebar">
      <div className="sidebar-header">
        <Filter size={20} />
        <h3>Categories</h3>
      </div>
      <ul className="category-list">
        {categories.map((cat) => (
          <li key={cat.name}>
            <button 
              className={`category-btn ${selectedCategory === cat.name ? 'active' : ''}`}
              onClick={() => setSelectedCategory(cat.name)}
            >
              <span className="cat-icon">{cat.icon}</span>
              <span className="cat-name">{cat.name}</span>
              <ChevronRight size={16} className="arrow" />
            </button>
          </li>
        ))}
      </ul>
      
      <div className="sidebar-promo">
        <h4>Organic Week</h4>
        <p>Get 20% off on all organic vegetables.</p>
        <button className="btn btn-secondary btn-sm">View Deals</button>
      </div>
    </aside>
  );
};

export default Sidebar;
