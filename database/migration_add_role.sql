-- Migration script to add role column to customers table
-- Run this on existing databases

-- Add role column if it doesn't exist
ALTER TABLE customers ADD COLUMN IF NOT EXISTS role VARCHAR(20) DEFAULT 'CUSTOMER';

-- Create admin user if not exists
-- Password: admin123 (BCrypt: $2b$10$GDksCSQ1v9gI9t4QzkTMduRjnCr6Gx2OgwwSVfv866egO6qgW04Km)
INSERT INTO customers (name, email, password_hash, phone, address, city, state, zip_code, loyalty_points, role)
SELECT 
    'Admin User',
    'admin@grocky.com',
    '$2b$10$GDksCSQ1v9gI9t4QzkTMduRjnCr6Gx2OgwwSVfv866egO6qgW04Km',
    '555-0100',
    '100 Admin St',
    'New York',
    'NY',
    '10001',
    0,
    'ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM customers WHERE email = 'admin@grocky.com');

-- Update existing customers to have explicit CUSTOMER role
UPDATE customers SET role = 'CUSTOMER' WHERE role IS NULL;
