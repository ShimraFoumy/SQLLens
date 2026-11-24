-- =============================================================================
-- SQLLens Sample Data Generator
-- Generates realistic, statistical sample datasets using PostgreSQL generate_series()
-- Ensures adequate cardinality for query planner behavior (Seq Scan, Index Scan, Joins)
-- =============================================================================

-- Clear existing data
TRUNCATE TABLE order_items, orders, products, employees, departments, customers RESTART IDENTITY CASCADE;

-- 1. Insert 20 Departments
INSERT INTO departments (name, building, budget) VALUES
('Engineering', 'Building A', 5000000.00),
('Product Management', 'Building A', 2200000.00),
('Marketing', 'Building B', 1800000.00),
('Sales', 'Building B', 3200000.00),
('Customer Support', 'Building C', 1200000.00),
('Finance', 'Building D', 1500000.00),
('Human Resources', 'Building D', 900000.00),
('Legal & Compliance', 'Building D', 850000.00),
('Information Technology', 'Building A', 2700000.00),
('Data & Analytics', 'Building A', 3100000.00),
('Research & Development', 'Building E', 4500000.00),
('Quality Assurance', 'Building A', 1100000.00),
('Operations', 'Building C', 2000000.00),
('Supply Chain', 'Building C', 1700000.00),
('Procurement', 'Building C', 950000.00),
('Security & Trust', 'Building A', 1600000.00),
('Corporate Communications', 'Building B', 650000.00),
('Public Relations', 'Building B', 500000.00),
('Design & UX', 'Building A', 1300000.00),
('Executive Office', 'Building E', 3800000.00);

-- 2. Insert 2,000 Employees
INSERT INTO employees (name, email, department_id, salary, hire_date)
SELECT
    'Employee ' || i,
    'employee.' || i || '@sqllens.io',
    1 + (i % 20),
    ROUND((35000 + (random() * 115000))::numeric, 2),
    DATE '2015-01-01' + (i % 3650)
FROM generate_series(1, 2000) AS i;

-- 3. Insert 5,000 Customers
INSERT INTO customers (name, email, city, country, created_at)
SELECT
    'Customer ' || i,
    'customer.' || i || '@example.com',
    (ARRAY[
        'Colombo', 'New York', 'London', 'Tokyo', 'Singapore',
        'Sydney', 'Berlin', 'Toronto', 'Paris', 'Mumbai',
        'Dubai', 'San Francisco', 'Chicago', 'Amsterdam', 'Dublin'
    ])[1 + (i % 15)],
    (ARRAY[
        'Sri Lanka', 'USA', 'UK', 'Japan', 'Singapore',
        'Australia', 'Germany', 'Canada', 'France', 'India',
        'UAE', 'USA', 'USA', 'Netherlands', 'Ireland'
    ])[1 + (i % 15)],
    TIMESTAMP '2020-01-01 00:00:00' + ((i * 37) % 1800 || ' days')::interval + ((i * 13) % 86400 || ' seconds')::interval
FROM generate_series(1, 5000) AS i;

-- 4. Insert 10,000 Products
INSERT INTO products (name, category, price, stock_quantity)
SELECT
    'Product ' || i || ' ' || (ARRAY['Pro', 'Max', 'Plus', 'Ultra', 'Standard', 'Lite', 'Elite', 'Prime'])[1 + (i % 8)],
    (ARRAY[
        'Electronics', 'Apparel', 'Home & Kitchen', 'Books',
        'Sports & Outdoors', 'Beauty & Health', 'Toys & Games',
        'Automotive', 'Groceries', 'Office Supplies'
    ])[1 + (i % 10)],
    ROUND((5.00 + (random() * 995.00))::numeric, 2),
    (10 + (random() * 490))::int
FROM generate_series(1, 10000) AS i;

-- 5. Insert 50,000 Orders
INSERT INTO orders (customer_id, order_date, total_amount, status)
SELECT
    1 + ((i * 17) % 5000),
    DATE '2021-01-01' + (i % 1460),
    ROUND((15.00 + (random() * 1485.00))::numeric, 2),
    (ARRAY['COMPLETED', 'SHIPPED', 'PROCESSING', 'PENDING', 'CANCELLED'])[1 + (i % 5)]
FROM generate_series(1, 50000) AS i;

-- 6. Insert 120,000 Order Items
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT
    1 + ((i * 7) % 50000),
    1 + ((i * 13) % 10000),
    1 + (i % 8),
    ROUND((5.00 + (random() * 495.00))::numeric, 2)
FROM generate_series(1, 120000) AS i;

-- Update optimizer statistics for accurate EXPLAIN calculations
ANALYZE departments;
ANALYZE employees;
ANALYZE customers;
ANALYZE products;
ANALYZE orders;
ANALYZE order_items;
