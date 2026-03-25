import React, { useEffect, useState } from 'react';
import Hero from '../components/Hero';
import ProductCard from '../components/ProductCard';
import { apiService } from '../services/apiService';
import { Sparkles, ArrowRight, Loader2 } from 'lucide-react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import '../styles/Home.css';

const Home: React.FC = () => {
  const [recommendedProducts, setRecommendedProducts] = useState<any[]>([]);
  const [trendingProducts, setTrendingProducts] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const { user } = useAuth();

  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      try {
        // Fetch trending products for everyone
        const trendingRes = await apiService.get('/recommendations/trending?limit=4');
        if (trendingRes.success) setTrendingProducts(trendingRes.data);

        // Fetch personalized recommendations if user is logged in
        if (user?.userId) {
          const recommendedRes = await apiService.get(`/recommendations/personal/${user.userId}?limit=4`);
          if (recommendedRes.success) setRecommendedProducts(recommendedRes.data);
        } else {
          // Use trending as recommendations if not logged in
          setRecommendedProducts(trendingRes.data);
        }
      } catch (err) {
        console.error("Failed to fetch data", err);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [user]);

  const categoryImages: { [key: string]: string } = {
    'Fruits': 'https://images.unsplash.com/photo-1619566636858-adb3ef26403b?auto=format&fit=crop&q=80&w=300',
    'Vegetables': 'https://images.unsplash.com/photo-1566385101042-1a000c1268c4?auto=format&fit=crop&q=80&w=300',
    'Dairy': 'https://images.unsplash.com/photo-1628088062854-d1870b4553da?auto=format&fit=crop&q=80&w=300',
    'Bakery': 'https://images.unsplash.com/photo-1509440159596-0249088772ff?auto=format&fit=crop&q=80&w=300'
  };

  return (
    <div className="home-page">
      <Hero />
      
      <section className="featured-categories container fade-in">
        <div className="section-header">
          <h2 className="section-title">Popular Categories</h2>
          <Link to="/products" className="view-all">View All <ArrowRight size={16} /></Link>
        </div>
        <div className="category-grid">
          {['Fruits', 'Vegetables', 'Dairy', 'Bakery'].map(cat => (
            <Link to={`/products?category=${cat}`} key={cat} className="cat-item card">
              <div className="cat-img">
                <img src={categoryImages[cat]} alt={cat} loading="lazy" />
              </div>
              <h3>{cat}</h3>
            </Link>
          ))}
        </div>
      </section>

      <section className="recommendations container">
        <div className="section-header">
          <div className="title-with-icon">
            <Sparkles size={28} color="#F39C12" />
            <h2 className="section-title">AI Recommended for You</h2>
          </div>
          <p>Personalized picks based on your taste profile</p>
        </div>
        
        {loading ? (
          <div className="loading-container">
            <Loader2 className="animate-spin" size={48} color="#2ECC71" />
            <p>Loading fresh picks...</p>
          </div>
        ) : (
          <div className="product-grid">
            {recommendedProducts.map(product => (
              <ProductCard key={product.id} product={product} />
            ))}
          </div>
        )}
      </section>

      <section className="trending container fade-in">
        <div className="section-header">
          <h2 className="section-title">Trending Now</h2>
          <p>The most popular items this week</p>
        </div>
        <div className="product-grid">
          {trendingProducts.map(product => (
            <ProductCard key={product.id} product={product} />
          ))}
        </div>
      </section>

      <section className="cta-banner container">
        <div className="banner-content card glass">
          <h2>Get 50% Off Your First Order</h2>
          <p>Join Grocky today and get fresh groceries delivered to your home for half the price.</p>
          <button className="btn btn-primary btn-lg">Join Now</button>
        </div>
      </section>
    </div>
  );
};

export default Home;
