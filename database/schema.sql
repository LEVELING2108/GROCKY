-- GROCKY Online Grocery Store Database Schema
-- PostgreSQL Database Script

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ============================================
-- CUSTOMERS TABLE
-- ============================================
CREATE TABLE customers (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    address TEXT,
    city VARCHAR(50),
    state VARCHAR(50),
    zip_code VARCHAR(10),
    loyalty_points INTEGER DEFAULT 0,
    ai_preference_profile JSONB,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- PRODUCTS TABLE
-- ============================================
CREATE TABLE products (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(200) NOT NULL,
    description TEXT,
    category VARCHAR(50) NOT NULL,
    subcategory VARCHAR(50),
    price DECIMAL(10, 2) NOT NULL,
    cost_price DECIMAL(10, 2),
    stock_quantity INTEGER DEFAULT 0,
    reorder_level INTEGER DEFAULT 10,
    unit VARCHAR(20) DEFAULT 'piece',
    brand VARCHAR(100),
    supplier VARCHAR(100),
    expiry_date DATE,
    image_url VARCHAR(500),
    is_available BOOLEAN DEFAULT true,
    discount_percentage DECIMAL(5, 2) DEFAULT 0,
    ai_demand_score DECIMAL(5, 2) DEFAULT 0,
    ai_reorder_suggestion BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- ORDERS TABLE
-- ============================================
CREATE TABLE orders (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    customer_id UUID NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    order_number VARCHAR(20) UNIQUE NOT NULL,
    status VARCHAR(30) DEFAULT 'PENDING',
    subtotal DECIMAL(10, 2) NOT NULL,
    tax_amount DECIMAL(10, 2) DEFAULT 0,
    delivery_fee DECIMAL(10, 2) DEFAULT 0,
    discount_amount DECIMAL(10, 2) DEFAULT 0,
    total_amount DECIMAL(10, 2) NOT NULL,
    delivery_address TEXT,
    delivery_city VARCHAR(50),
    delivery_state VARCHAR(50),
    delivery_zip VARCHAR(10),
    delivery_instructions TEXT,
    scheduled_delivery_date TIMESTAMP,
    ai_predicted_delivery_time INTERVAL,
    payment_status VARCHAR(20) DEFAULT 'PENDING',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    delivered_at TIMESTAMP
);

-- ============================================
-- ORDER ITEMS TABLE
-- ============================================
CREATE TABLE order_items (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE RESTRICT,
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(10, 2) NOT NULL,
    total_price DECIMAL(10, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- PAYMENTS TABLE
-- ============================================
CREATE TABLE payments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    payment_method VARCHAR(30) NOT NULL,
    payment_gateway VARCHAR(50),
    amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    transaction_id VARCHAR(100),
    gateway_response JSONB,
    processed_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- INVENTORY LOG TABLE
-- ============================================
CREATE TABLE inventory_log (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    change_type VARCHAR(20) NOT NULL,
    quantity_change INTEGER NOT NULL,
    quantity_before INTEGER NOT NULL,
    quantity_after INTEGER NOT NULL,
    reason VARCHAR(100),
    reference_id UUID,
    created_by VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- ANALYTICS TABLE
-- ============================================
CREATE TABLE analytics (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    metric_type VARCHAR(50) NOT NULL,
    metric_name VARCHAR(100) NOT NULL,
    metric_value DECIMAL(15, 2) NOT NULL,
    metadata JSONB,
    recorded_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- REVIEWS TABLE
-- ============================================
CREATE TABLE reviews (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    customer_id UUID NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    rating INTEGER NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    is_verified_purchase BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(product_id, customer_id)
);

-- ============================================
-- CART TABLE
-- ============================================
CREATE TABLE cart (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    customer_id UUID NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    product_id UUID NOT NULL REFERENCES products(id) ON DELETE CASCADE,
    quantity INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(customer_id, product_id)
);

-- ============================================
-- INDEXES FOR PERFORMANCE
-- ============================================
CREATE INDEX idx_customers_email ON customers(email);
CREATE INDEX idx_customers_loyalty ON customers(loyalty_points);
CREATE INDEX idx_products_category ON products(category);
CREATE INDEX idx_products_price ON products(price);
CREATE INDEX idx_products_stock ON products(stock_quantity);
CREATE INDEX idx_products_ai_score ON products(ai_demand_score);
CREATE INDEX idx_orders_customer ON orders(customer_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_created ON orders(created_at);
CREATE INDEX idx_order_items_order ON order_items(order_id);
CREATE INDEX idx_order_items_product ON order_items(product_id);
CREATE INDEX idx_payments_order ON payments(order_id);
CREATE INDEX idx_payments_status ON payments(status);
CREATE INDEX idx_inventory_product ON inventory_log(product_id);
CREATE INDEX idx_inventory_created ON inventory_log(created_at);
CREATE INDEX idx_analytics_type ON analytics(metric_type);
CREATE INDEX idx_analytics_date ON analytics(recorded_date);

-- ============================================
-- TRIGGERS FOR UPDATED_AT
-- ============================================
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_customers_updated_at BEFORE UPDATE ON customers
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_products_updated_at BEFORE UPDATE ON products
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_orders_updated_at BEFORE UPDATE ON orders
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_cart_updated_at BEFORE UPDATE ON cart
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ============================================
-- SAMPLE DATA FOR TESTING
-- ============================================

-- Sample Customers
INSERT INTO customers (name, email, password_hash, phone, address, city, state, zip_code, loyalty_points) VALUES
('John Doe', 'john@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '555-0101', '123 Main St', 'New York', 'NY', '10001', 150),
('Jane Smith', 'jane@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '555-0102', '456 Oak Ave', 'Los Angeles', 'CA', '90001', 280),
('Bob Wilson', 'bob@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '555-0103', '789 Pine Rd', 'Chicago', 'IL', '60601', 75);

-- Sample Products
INSERT INTO products (name, description, category, subcategory, price, cost_price, stock_quantity, reorder_level, unit, brand, supplier, ai_demand_score) VALUES
('Organic Bananas', 'Fresh organic bananas, 1 bunch', 'Produce', 'Fruits', 2.99, 1.50, 100, 20, 'bunch', 'Fresh Farms', 'Local Supplier', 85.5),
('Whole Milk', 'Fresh whole milk, 1 gallon', 'Dairy', 'Milk', 3.49, 2.00, 50, 15, 'gallon', 'Dairy Best', 'Milk Co', 92.3),
('Bread - Whole Wheat', 'Whole wheat bread loaf', 'Bakery', 'Bread', 2.79, 1.25, 30, 10, 'loaf', 'Baker''s Choice', 'Bread Inc', 78.9),
('Chicken Breast', 'Boneless skinless chicken breast', 'Meat', 'Poultry', 8.99, 5.50, 40, 15, 'lb', 'Premium Meat', 'Meat Suppliers', 88.7),
('Rice - Basmati', 'Premium basmati rice', 'Pantry', 'Grains', 12.99, 7.00, 60, 20, 'bag', 'Royal Rice', 'Grain Traders', 71.2),
('Olive Oil', 'Extra virgin olive oil, 500ml', 'Pantry', 'Oils', 9.99, 5.50, 25, 10, 'bottle', 'Mediterranean', 'Oil Imports', 65.8),
('Eggs - Large', 'Farm fresh large eggs, 12 count', 'Dairy', 'Eggs', 4.49, 2.75, 80, 25, 'dozen', 'Farm Fresh', 'Local Farms', 95.1),
('Tomatoes', 'Vine ripened tomatoes', 'Produce', 'Vegetables', 3.99, 2.00, 70, 20, 'lb', 'Fresh Farms', 'Local Supplier', 82.4),
('Cheese - Cheddar', 'Sharp cheddar cheese block', 'Dairy', 'Cheese', 5.99, 3.50, 35, 12, 'block', 'Cheese Masters', 'Dairy Direct', 73.6),
('Salmon Fillet', 'Fresh Atlantic salmon', 'Seafood', 'Fish', 14.99, 9.00, 20, 8, 'lb', 'Ocean Fresh', 'Seafood Co', 69.4);

-- Sample Orders
INSERT INTO orders (customer_id, order_number, status, subtotal, tax_amount, delivery_fee, total_amount, delivery_address, delivery_city, delivery_state, delivery_zip) VALUES
((SELECT id FROM customers WHERE email = 'john@example.com'), 'ORD-2024-0001', 'DELIVERED', 25.47, 2.04, 5.99, 33.50, '123 Main St', 'New York', 'NY', '10001'),
((SELECT id FROM customers WHERE email = 'jane@example.com'), 'ORD-2024-0002', 'SHIPPED', 42.96, 3.44, 7.99, 54.39, '456 Oak Ave', 'Los Angeles', 'CA', '90001'),
((SELECT id FROM customers WHERE email = 'bob@example.com'), 'ORD-2024-0003', 'PENDING', 18.48, 1.48, 4.99, 24.95, '789 Pine Rd', 'Chicago', 'IL', '60601');

-- Sample Order Items
INSERT INTO order_items (order_id, product_id, quantity, unit_price, total_price) VALUES
((SELECT id FROM orders WHERE order_number = 'ORD-2024-0001'), (SELECT id FROM products WHERE name = 'Organic Bananas'), 2, 2.99, 5.98),
((SELECT id FROM orders WHERE order_number = 'ORD-2024-0001'), (SELECT id FROM products WHERE name = 'Whole Milk'), 3, 3.49, 10.47),
((SELECT id FROM orders WHERE order_number = 'ORD-2024-0001'), (SELECT id FROM products WHERE name = 'Bread - Whole Wheat'), 2, 2.79, 5.58),
((SELECT id FROM orders WHERE order_number = 'ORD-2024-0001'), (SELECT id FROM products WHERE name = 'Eggs - Large'), 1, 4.49, 4.49),
((SELECT id FROM orders WHERE order_number = 'ORD-2024-0002'), (SELECT id FROM products WHERE name = 'Chicken Breast'), 2, 8.99, 17.98),
((SELECT id FROM orders WHERE order_number = 'ORD-2024-0002'), (SELECT id FROM products WHERE name = 'Rice - Basmati'), 1, 12.99, 12.99),
((SELECT id FROM orders WHERE order_number = 'ORD-2024-0002'), (SELECT id FROM products WHERE name = 'Olive Oil'), 1, 9.99, 9.99),
((SELECT id FROM orders WHERE order_number = 'ORD-2024-0003'), (SELECT id FROM products WHERE name = 'Tomatoes'), 2, 3.99, 7.98),
((SELECT id FROM orders WHERE order_number = 'ORD-2024-0003'), (SELECT id FROM products WHERE name = 'Cheese - Cheddar'), 1, 5.99, 5.99),
((SELECT id FROM orders WHERE order_number = 'ORD-2024-0003'), (SELECT id FROM products WHERE name = 'Bread - Whole Wheat'), 1, 2.79, 2.79);

-- Sample Payments
INSERT INTO payments (order_id, payment_method, payment_gateway, amount, status, transaction_id) VALUES
((SELECT id FROM orders WHERE order_number = 'ORD-2024-0001'), 'CREDIT_CARD', 'stripe', 33.50, 'COMPLETED', 'txn_1234567890'),
((SELECT id FROM orders WHERE order_number = 'ORD-2024-0002'), 'CREDIT_CARD', 'stripe', 54.39, 'COMPLETED', 'txn_0987654321'),
((SELECT id FROM orders WHERE order_number = 'ORD-2024-0003'), 'PAYPAL', 'paypal', 24.95, 'PENDING', 'txn_abcdef1234');

-- Sample Reviews
INSERT INTO reviews (product_id, customer_id, rating, comment) VALUES
((SELECT id FROM products WHERE name = 'Organic Bananas'), (SELECT id FROM customers WHERE email = 'john@example.com'), 5, 'Excellent quality, very fresh!'),
((SELECT id FROM products WHERE name = 'Whole Milk'), (SELECT id FROM customers WHERE email = 'jane@example.com'), 4, 'Good milk, stays fresh longer.'),
((SELECT id FROM products WHERE name = 'Chicken Breast'), (SELECT id FROM customers WHERE email = 'bob@example.com'), 5, 'Premium quality chicken.');

-- Sample Analytics Data
INSERT INTO analytics (metric_type, metric_name, metric_value, recorded_date) VALUES
('SALES', 'daily_revenue', 1250.50, CURRENT_DATE - 7),
('SALES', 'daily_revenue', 1430.75, CURRENT_DATE - 6),
('SALES', 'daily_revenue', 1180.25, CURRENT_DATE - 5),
('SALES', 'daily_revenue', 1620.00, CURRENT_DATE - 4),
('SALES', 'daily_revenue', 1890.50, CURRENT_DATE - 3),
('SALES', 'daily_revenue', 2150.25, CURRENT_DATE - 2),
('SALES', 'daily_revenue', 1750.75, CURRENT_DATE - 1),
('ORDERS', 'daily_orders', 45, CURRENT_DATE - 7),
('ORDERS', 'daily_orders', 52, CURRENT_DATE - 6),
('ORDERS', 'daily_orders', 38, CURRENT_DATE - 5),
('ORDERS', 'daily_orders', 61, CURRENT_DATE - 4),
('ORDERS', 'daily_orders', 73, CURRENT_DATE - 3),
('ORDERS', 'daily_orders', 85, CURRENT_DATE - 2),
('ORDERS', 'daily_orders', 67, CURRENT_DATE - 1),
('CUSTOMERS', 'new_customers', 12, CURRENT_DATE - 7),
('CUSTOMERS', 'new_customers', 8, CURRENT_DATE - 6),
('CUSTOMERS', 'new_customers', 15, CURRENT_DATE - 5),
('CUSTOMERS', 'new_customers', 10, CURRENT_DATE - 4),
('CUSTOMERS', 'new_customers', 18, CURRENT_DATE - 3),
('CUSTOMERS', 'new_customers', 22, CURRENT_DATE - 2),
('CUSTOMERS', 'new_customers', 14, CURRENT_DATE - 1);
